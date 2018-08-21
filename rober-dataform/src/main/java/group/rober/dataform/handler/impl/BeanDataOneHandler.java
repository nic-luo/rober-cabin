package group.rober.dataform.handler.impl;

import group.rober.dataform.handler.DataOneHandler;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.util.DataFormValidateUtils;
import group.rober.dataform.validator.ValidateResult;
import group.rober.runtime.kit.ClassKit;
import group.rober.sql.core.DataQuery;

import java.util.Map;

public class BeanDataOneHandler<T> extends BeanDataHandler<T> implements DataOneHandler<T> {

    public void initDataForm(DataForm dataForm) {

    }

    public T createDataObject(DataForm dataForm) {
        return (T) ClassKit.newInstance(dataForm.getDataModel());
    }

    public T query(DataForm dataForm, Map<String, ?> queryParameters) {
        String sql = dataForm.getQuery().buildQuerySql(false);

        Class<T> clazz = getFormClass(dataForm);
//        dataAccessor.getDataQuery().setRowMapper(getRowMapper(dataForm,clazz));
//        return dataAccessor.selectOne(clazz, sql, queryParameters);
        DataQuery dataQuery = dataAccessor.getDataQuery();
        T ret = dataQuery.exec(getRowMapper(dataForm,clazz),()->{
            return dataQuery.selectOne(clazz, sql, queryParameters);
        });
        return ret;
    }

    public int insert(DataForm dataForm, T object) {
        return dataAccessor.insert(object);
    }

    public int update(DataForm dataForm, T object) {
        return dataAccessor.update(object);
    }

    public int save(DataForm dataForm, T object) {
        return dataAccessor.save(object);
    }

    public int delete(DataForm dataForm, T object) {
        return dataAccessor.delete(object);
    }

    public ValidateResult validate(DataForm dataForm, T object) {
        return DataFormValidateUtils.validateOne(dataForm,this,object);
    }

}
