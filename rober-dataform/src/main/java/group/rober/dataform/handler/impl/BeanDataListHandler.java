package group.rober.dataform.handler.impl;

import group.rober.dataform.exception.DataFormException;
import group.rober.dataform.handler.DataListHandler;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.util.DataFormUtils;
import group.rober.dataform.util.DataFormValidateUtils;
import group.rober.dataform.validator.ValidateResult;
import group.rober.runtime.kit.*;
import group.rober.runtime.lang.ValueObject;
import group.rober.sql.core.DataQuery;
import group.rober.sql.core.PaginationData;
import group.rober.sql.core.PaginationQuery;
import group.rober.sql.dialect.SqlDialectType;
import group.rober.sql.kit.SQLInjector;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.util.*;

public class BeanDataListHandler<T> extends BeanDataHandler<T> implements DataListHandler<T> {


    public void initDataForm(DataForm dataForm) {

    }

    private Map<String,String> propToColForSortMap(Class<T> clazz,DataForm dataForm,Map<String, ?> sortMap){
        Map<String,String> sortRules = new LinkedCaseInsensitiveMap<String>();

        Iterator<String> iterator = sortMap.keySet().iterator();
        while(iterator.hasNext()){
            String propCode = iterator.next();
            DataFormElement element = dataForm.getElement(propCode);
            if(element == null)continue;
            String fieldName = StringKit.nvl(element.getColumn(),element.getCode());
            String column = JpaKit.getColumn(clazz,fieldName);
            if(StringKit.isBlank(column)){
                column = fieldName;
            }
            if(StringKit.isBlank(column)){
                column = JpaKit.getColumn(clazz,propCode);
            }
            sortRules.put(column,ValueObject.valueOf(sortMap.get(propCode)).strValue());
        }

        return sortRules;
    }

    protected ValueObject getFilterValue(DataForm dataForm,Map<String, ?> filterData,String code){
        return FilterWhereProcessor.getFilterValue(dataForm,filterData,code);
    }

    public PaginationData<T> query(DataForm dataForm, Map<String, ?> queryParameters, Map<String, ?> filterParameters, Map<String, ?> sortMap, int pageSize, int pageIndex) {
        Map<String,?> sqlParameter = queryParameters;
        //如果启用了快速搜索
        //处理筛选器以及快速搜索器
        Map<String,Object> quickParameter = MapKit.newHashMap();
        FilterWhereProcessor.parseFilter(dataForm,filterParameters,quickParameter,sqlParameter);

        Class<T> clazz = getFormClass(dataForm);
        String sql = dataForm.getQuery().buildQuerySql(false);
        //如果有排序数据，则把排序注入SQL查询中去
        if(sortMap!=null&&sortMap.size()>0){
            Map<String,String> sortRules = propToColForSortMap(clazz,dataForm,sortMap);//把传入的属性转为字段名
            SqlDialectType dialectType = dataAccessor.getDataQuery().getSqlDialectType();
            try{
                sql = SQLInjector.injectSQLOrder(dialectType,sql,sortRules);
            }catch(Exception e){
                throw new DataFormException(e,"SQL解析出错,SQL[{0}]",sql);
            }
        }



        //分页查询参数处理
        PaginationQuery query = DataFormUtils.buildPaginationQuery(dataForm,sql,pageIndex,pageSize);
        query.getParameterMap().putAll(queryParameters);
        query.getParameterMap().putAll(quickParameter);

//        dataAccessor.getDataQuery().setRowMapper(getRowMapper(dataForm,clazz));
//        PaginationData<T> ret = dataAccessor.selectListPagination(clazz,query);
        DataQuery dataQuery = dataAccessor.getDataQuery();
        PaginationData<T> ret = dataQuery.exec(getRowMapper(dataForm,clazz),()->{
            return dataQuery.selectListPagination(clazz,query);
        });
        DataFormUtils.fixSummaryCodeName(dataForm,ret);

        return ret;
    }

    public int insert(DataForm dataForm, List<T> dataList) {
        return dataAccessor.insert(dataList);
    }

    public int update(DataForm dataForm, List<T> dataList) {
        return dataAccessor.update(dataList);
    }

    public int save(DataForm dataForm, List<T> dataList) {
        return dataAccessor.save(dataList);
    }

    public Integer delete(DataForm dataForm, List<T> dataList) {
        return dataAccessor.delete(dataList);
    }


    public List<ValidateResult> validate(DataForm dataForm, List<T> dataList) {
        return DataFormValidateUtils.validateList(dataForm,this,dataList);
    }

}
