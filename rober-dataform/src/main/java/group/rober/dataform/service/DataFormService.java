package group.rober.dataform.service;

import group.rober.dataform.mapper.DataFormMapper;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.runtime.kit.JpaKit;
import group.rober.runtime.kit.SQLKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.converter.impl.UnderlinedNameConverter;
import group.rober.sql.core.SqlQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DataForm功能实现
 * Created by tisir yangsong158@qq.com on 2017-05-30
 */
@Service
public class DataFormService {
    @Autowired
    private DataFormMapper dataFormMapper;

    public DataFormMapper getDataFormMapper() {
        return dataFormMapper;
    }

    public void setDataFormMapper(DataFormMapper dataFormMapper) {
        this.dataFormMapper = dataFormMapper;
    }

    public void clearCacheAll() {
        dataFormMapper.clearCacheAll();
    }

    public void clearCacheItem(String formId) {
        dataFormMapper.clearCacheItem(formId);
    }

    /**
     * 仅取显示模板目录列表
     *
     * @return list
     */
    public List<DataForm> getBriefDataFormList(){
        return dataFormMapper.getAllDataForms();
    }

    public DataForm getDataForm(String dataform){
        int lastDotIdx = dataform.lastIndexOf("-"); //URL路径中,多个点号传输会出问题
        DataForm dataForm = null;
        if(lastDotIdx>0){
            String pack = dataform.substring(0,lastDotIdx);
            String formId = dataform.substring(lastDotIdx+1);
            dataForm = dataFormMapper.getDataForm(pack,formId);
        }else{
            dataForm = dataFormMapper.getDataForm("",dataform);
        }
        ValidateKit.notNull(dataForm, "DataForm不存在,form=" + dataform);

        fillSelectedItems(dataForm);

        return dataForm;
    }

    /**
     * 根据显示模板列出的字段，填写select语句
     *
     * @param dataForm
     */
    private void fillSelectedItems(DataForm dataForm){
        SqlQuery query = dataForm.getQuery();
        List<PairBond> items = query.getSelectItems();
        if(items==null) items = new ArrayList<PairBond>();
        query.setSelectItems(items);
        if(items.size()>0)return ;
        List<DataFormElement> elements = dataForm.getElements();
        UnderlinedNameConverter converter = new UnderlinedNameConverter();

        Set<String> scannedCols = new HashSet<String>();
        for(DataFormElement element : elements){
            String column = element.getColumn();
            String columnFixed = column ;
            String alias = element.getCode();
            if(StringKit.isBlank(column)&&StringKit.isBlank(alias))continue;

            //如果是虚字段，并且字段别名不为空的情况下，把虚字段AS一下
            if(SQLKit.isConstColumn(column)&&StringKit.isNotBlank(alias)){
                column = String.join(" AS ",column,StringKit.camelToUnderline(alias));
            //如果字段名重复，也要作AS,否则，在select * (Name,Name 。。。）时，会出SQL语法错误
            }else{
                if(StringKit.isBlank(column)){
                    column = converter.getColumnName(alias);
                }
                if (!StringKit.isBlank(element.getTable()))  {
                    column = String.join(".",element.getTable(),column);
                }
                //名称重复字段的处理
                if(scannedCols.contains(columnFixed) && StringKit.isNotBlank(alias)){
                    column = String.join(" AS ",column,StringKit.camelToUnderline(alias));
                }

                if(StringKit.isBlank(alias)){
                    alias = converter.getPropertyName(column);
                }
            }
            items.add(new PairBond(alias,column));
            scannedCols.add(columnFixed);
        }
    }
}
