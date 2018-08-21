package group.rober.dataform.handler.impl;

import group.rober.dataform.handler.DataOneHandler;
import group.rober.dataform.model.DataForm;
import group.rober.dataform.model.DataFormElement;
import group.rober.dataform.util.DataFormValidateUtils;
import group.rober.dataform.validator.ValidateResult;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.lang.MapData;
import group.rober.sql.core.MapDataQuery;
import group.rober.sql.core.MapDataUpdater;
import group.rober.sql.core.MapDataExecutor;

import java.util.List;
import java.util.Map;

/**
 * Map单条数据处理
 */
public class MapDataOneHandler extends MapDataHandler implements DataOneHandler<MapData> {

    protected MapData params;

    public void initDataForm(DataForm dataForm) {

    }

    public MapData getParams(DataForm dataForm) {
        return params;
    }

    public MapData createDataObject(DataForm dataForm) {
        MapData object = new MapData();
        List<DataFormElement> elements = dataForm.getElements();
        for (DataFormElement element : elements) {
            object.put(element.getCode(), null);
        }
        return object;
    }

    public MapData query(DataForm dataForm, Map<String, ?> queryParameters) {
        validateDataForm(dataForm);
        String query = dataForm.getQuery().buildQuerySql(false);

        MapDataQuery dataQuery = mapDataAccessor.getMapDataQuery();
//        MapData row = mapDataAccessor.getMapDataQuery()
//                .selectOne(query, queryParameters);
        MapData row = dataQuery.exec(getRowMapper(dataForm),()->{
            return dataQuery.selectOne(query, queryParameters);
        });

        MapData ret = remoldMapData(dataForm, row);

        return ret;
    }


    public int insert(DataForm dataForm, MapData object) {
        validateDataForm(dataForm);
        final MapData dbMapdata = getDbMapData(dataForm, object);
        togglePrimaryKey(dataForm, dbMapdata);

        MapDataUpdater updater = mapDataAccessor.getMapDataUpdater();
        //在模板类中定义操作，这样，可以把其他需要统一处理的东西放到被调用的方法中处理
        return updater.exec(getNameConverter(dataForm), new MapDataExecutor<Integer>() {
            public Integer impl() {
                return updater.insert(dataForm.getDataModel(), dbMapdata);
            }
        });
    }


    public int update(DataForm dataForm, MapData object) {
        validateDataForm(dataForm);

        String table = dataForm.getDataModel();
        final MapData dbMapdata = getDbMapData(dataForm, object);
        final MapData dbPkMapdata = getDbPkMapData(dataForm, object);

        MapDataUpdater updater = mapDataAccessor.getMapDataUpdater();
        //在模板类中定义操作，这样，可以把其他需要统一处理的东西放到被调用的方法中处理
        return updater.exec(getNameConverter(dataForm), new MapDataExecutor<Integer>() {
            public Integer impl() {
                return updater.update(table, dbMapdata, dbPkMapdata);
            }
        });
    }

    public int save(DataForm dataForm, MapData object) {
        validateDataForm(dataForm);

        final String table = dataForm.getDataModel();
        final MapData dbMapdata = getDbMapData(dataForm, object);
        final MapData dbPkMapdata = getDbPkMapData(dataForm, object);
        ValidateKit.notBlank(table,"当模型为MapData时，请填写显示模板[数据实体:DATA_MODEL]字段");

        togglePrimaryKey(dataForm, dbMapdata);

        MapDataUpdater updater = mapDataAccessor.getMapDataUpdater();
        //在模板类中定义操作，这样，可以把其他需要统一处理的东西放到被调用的方法中处理
        return updater.exec(getNameConverter(dataForm), new MapDataExecutor<Integer>() {
            public Integer impl() {
                return updater.save(table, dbMapdata, dbPkMapdata);
            }
        });
    }

    public int delete(DataForm dataForm, MapData object) {
        validateDataForm(dataForm);

        final String table = dataForm.getDataModel();
        final MapData dbPkMapdata = getDbPkMapData(dataForm, object);

        MapDataUpdater updater = mapDataAccessor.getMapDataUpdater();
        //在模板类中定义操作，这样，可以把其他需要统一处理的东西放到被调用的方法中处理
        return updater.exec(getNameConverter(dataForm), new MapDataExecutor<Integer>() {
            public Integer impl() {
                return updater.delete(table, dbPkMapdata);
            }
        });
    }

    public ValidateResult validate(DataForm dataForm, MapData object) {
        return DataFormValidateUtils.validateOne(dataForm, this, object);
    }
}
