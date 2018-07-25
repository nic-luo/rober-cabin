package group.rober.dataform.handler.impl;

import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.util.DataFormUtils;
import group.rober.dataform.validator.ValidateResult;
import group.rober.runtime.kit.*;
import group.rober.sql.core.DataAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BeanDataHandler<T> {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected DataAccessor dataAccessor;

    public DataAccessor getDataAccessor() {
        return dataAccessor;
    }

    public void setDataAccessor(DataAccessor dataAccessor) {
        this.dataAccessor = dataAccessor;
    }

    protected Class<T> getFormClass(DataForm dataForm){
        ValidateKit.notNull(dataForm.getDataModel(),"显示模板{0}数据模型的数据类型为JavaBean，但没有配置数据实体类",dataForm.getId());
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(dataForm.getDataModel());
        } catch (ClassNotFoundException e) {
            throw new DataFormException(e,"显示模板[{0}]的数据实体类[{1}]不存在",dataForm.getId(),dataForm.getDataModel());
        }
        return clazz;
    }

    private Map<String,Integer> buildColumnIndexMap(Map<String,Integer> columnIndexMap,ResultSetMetaData meta) throws SQLException {
        int count = meta.getColumnCount();
        for(int i=1;i<=count;i++){
            String column = StringKit.nvl(meta.getColumnName(i),meta.getColumnLabel(i));
            columnIndexMap.put(column,i);
        }
        return columnIndexMap;
    }

    protected RowMapper<T> getRowMapper(DataForm dataForm,Class<T> clazz){
        T rowTemplate = BeanUtils.instantiateClass(clazz);
        List<DataFormElement>  elements = dataForm.getElements();

        Map<String,Integer> columnIndexMap = new HashMap<String,Integer>();
        RowMapper<T> rowMapper = new RowMapper<T>() {
            public T mapRow(ResultSet rs, int rowNum) throws SQLException {
                if(columnIndexMap.isEmpty()){
                    buildColumnIndexMap(columnIndexMap,rs.getMetaData());
                }

                T row = BeanKit.deepClone(rowTemplate);

                elements.forEach(element->{
                    String code = StringKit.nvl(element.getCode(),StringKit.underlineToCamel(element.getColumn()));
//                    String column = StringKit.nvl(element.getColumn(),StringKit.camelToUnderline(code));
                    String column = element.getColumn();
                    if((SQLKit.isConstColumn(column) || StringKit.isBlank(column))
                            && StringKit.isNotBlank(code)){
                        column = StringKit.camelToUnderline(code);
                    }

                    if(!columnIndexMap.containsKey(column))return;
                    Integer index = columnIndexMap.get(column);
                    try {
                        Object value = JdbcKit.getResultSetValue(rs,index);
                        BeanKit.setPropertyValue(row,code,value);
                    } catch (SQLException e) {
                        throw new DataFormException("",e);
                    }
                });


                return row;
            }
        };

        return rowMapper;
    }

}
