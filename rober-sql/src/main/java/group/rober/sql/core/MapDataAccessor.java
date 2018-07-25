package group.rober.sql.core;

import group.rober.runtime.lang.MapData;
import group.rober.sql.listener.InsertListener;
import group.rober.sql.listener.UpdateListener;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;

/**
 * 为方便使用，把数据查询和数据读取放到一起，成为数据存取器
 */
public class MapDataAccessor {
    protected MapDataQuery mapDataQuery;
    protected MapDataUpdater mapDataUpdater;

    public MapDataAccessor(MapDataQuery mapDataQuery, MapDataUpdater mapDataUpdater) {
        this.mapDataQuery = mapDataQuery;
        this.mapDataUpdater = mapDataUpdater;
    }

    public MapDataQuery getMapDataQuery() {
        return mapDataQuery;
    }

    public MapDataUpdater getMapDataUpdater() {
        return mapDataUpdater;
    }

    public List<MapData> selectList(String sql, Map<String, ?> parameter) {
        return mapDataQuery.selectList(sql, parameter);
    }

    public List<MapData> selectList(String sql) {
        return mapDataQuery.selectList(sql);
    }

    public List<MapData> selectList(String sql, String k1, Object v1) {
        return mapDataQuery.selectList(sql, k1, v1);
    }

    public List<MapData> selectList(String sql, String k1, Object v1, String k2, Object v2) {
        return mapDataQuery.selectList(sql, k1, v1, k2, v2);
    }

    public List<MapData> selectList(String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return mapDataQuery.selectList(sql, k1, v1, k2, v2, k3, v3);
    }

    public MapData selectOne(String sql, Map<String, ?> parameter) {
        return mapDataQuery.selectOne(sql, parameter);
    }

    public MapData selectOne(String sql) {
        return mapDataQuery.selectOne(sql);
    }

    public MapData selectOne(String sql, String k1, Object v1) {
        return mapDataQuery.selectOne(sql, k1, v1);
    }

    public MapData selectOne(String sql, String k1, Object v1, String k2, Object v2) {
        return mapDataQuery.selectOne(sql, k1, v1, k2, v2);
    }

    public MapData selectOne(String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return mapDataQuery.selectOne(sql, k1, v1, k2, v2, k3, v3);
    }

    public PaginationData<MapData> selectListPagination(PaginationQuery query) {
        return mapDataQuery.selectListPagination(query);
    }

    public PaginationData<MapData> selectListPagination(String sql, Map<String, ?> paramMap, int index, int size) {
        return mapDataQuery.selectListPagination(sql, paramMap, index, size);
    }

    public <T> PaginationData<T> selectListPagination(PaginationQuery query, RowMapper<T> rowMapper) {
        return mapDataQuery.selectListPagination(query, rowMapper);
    }

    public Integer selectCount(String sql, Map<String, ?> paramMap) {
        return mapDataQuery.selectCount(sql, paramMap);
    }

    public Integer selectCount(String sql, String k1, Object v1) {
        return mapDataQuery.selectCount(sql, k1, v1);
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2) {
        return mapDataQuery.selectCount(sql, k1, v1, k2, v2);
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return mapDataQuery.selectCount(sql, k1, v1, k2, v2, k3, v3);
    }

    public int insert(String table, MapData entity, KeyHolder keyholder, InsertListener<MapData> listener) {
        return mapDataUpdater.insert(table, entity, keyholder, listener);
    }

    public int insert(String table, MapData entity, InsertListener<MapData> listener) {
        return mapDataUpdater.insert(table, entity, listener);
    }

    public int insert(String table, MapData entity) {
        return mapDataUpdater.insert(table, entity);
    }

    public int insert(String table, List<MapData> entityList, InsertListener<List<MapData>> listener) {
        return mapDataUpdater.insert(table, entityList, listener);
    }

    public int insert(String table, List<MapData> entityList) {
        return mapDataUpdater.insert(table, entityList);
    }

    public int update(String table, MapData entity, MapData keyAttributes, UpdateListener<MapData> listener) {
        return mapDataUpdater.update(table, entity, keyAttributes, listener);
    }

    public int update(String table, MapData entity, MapData keyAttributes) {
        return mapDataUpdater.update(table, entity, keyAttributes);
    }

    public int update(String table, List<MapData> entityList, List<MapData> keyAttributesList, UpdateListener<List<MapData>> listener) {
        return mapDataUpdater.update(table, entityList, keyAttributesList, listener);
    }

    public int update(String table, List<MapData> entity, List<MapData> keyAttributes) {
        return mapDataUpdater.update(table, entity, keyAttributes);
    }

    public int save(String table, MapData entity, MapData keyAttributes, KeyHolder keyholder, InsertListener<MapData> insertListener, UpdateListener<MapData> updateListener) {
        return mapDataUpdater.save(table, entity, keyAttributes, keyholder, insertListener, updateListener);
    }

    public int save(String table, MapData entity, MapData keyAttributes, KeyHolder keyHolder) {
        return mapDataUpdater.save(table, entity, keyAttributes, keyHolder);
    }

    public int save(String table, MapData entity, MapData keyAttributes) {
        return mapDataUpdater.save(table, entity, keyAttributes);
    }

    public int save(String table, List<MapData> entityList, List<MapData> keyAttributesList, InsertListener<List<MapData>> insertListener, UpdateListener<List<MapData>> updateListener) {
        return mapDataUpdater.save(table, entityList, keyAttributesList, insertListener, updateListener);
    }

    public int save(String table, List<MapData> entityList, List<MapData> keyAttributesList) {
        return mapDataUpdater.save(table, entityList, keyAttributesList);
    }

    public int execute(String sql) {
        return mapDataUpdater.execute(sql);
    }

    public int execute(String sql, Map<String, ?> paramMap) {
        return mapDataUpdater.execute(sql, paramMap);
    }
}
