package group.rober.sql.core;

import group.rober.sql.dialect.SqlDialect;
import group.rober.sql.listener.DeleteListener;
import group.rober.sql.listener.InsertListener;
import group.rober.sql.listener.UpdateListener;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Map;

public class DataAccessor {
    protected DataQuery dataQuery;
    protected DataUpdater dataUpdater;

    public DataAccessor(DataQuery dataQuery, DataUpdater dataUpdater) {
        this.dataQuery = dataQuery;
        this.dataUpdater = dataUpdater;
    }

    public DataUpdater getDataUpdater() {
        return dataUpdater;
    }

    public void setDataUpdater(DataUpdater dataUpdater) {
        this.dataUpdater = dataUpdater;
    }

    public DataQuery getDataQuery() {
        return dataQuery;
    }

    public void setDataQuery(DataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    public <T> List<T> selectList(Class<T> classType, String sql, Map<String, ?> parameter) {
        return dataQuery.selectList(classType, sql, parameter);
    }

    public <T> List<T> selectList(Class<T> classType, String sql) {
        return dataQuery.selectList(classType, sql);
    }

    public <T> List<T> selectList(Class<T> classType, String sql, String k1, Object v1) {
        return dataQuery.selectList(classType, sql, k1, v1);
    }

    public <T> List<T> selectList(Class<T> classType, String sql, String k1, Object v1, String k2, Object v2) {
        return dataQuery.selectList(classType, sql, k1, v1, k2, v2);
    }

    public <T> List<T> selectList(Class<T> classType, String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return dataQuery.selectList(classType, sql, k1, v1, k2, v2, k3, v3);
    }

    public <T> T selectOne(Class<T> classType, String sql, Map<String, ?> parameter) {
        return dataQuery.selectOne(classType, sql, parameter);
    }

    public <T> T selectOneById(Class<T> classType, Map<String, ?> paraMap) {
        return dataQuery.selectOneById(classType, paraMap);
    }

    public <T, V> T selectOneById(Class<T> classType, V... values) {
        return dataQuery.selectOneById(classType, values);
    }

    public <T> boolean selectExistsById(Class<T> classType, Map<String, ?> paraMap) {
        return dataQuery.selectExistsById(classType, paraMap);
    }

    public <T, V> boolean selectExistsById(Class<T> classType, V... values) {
        return dataQuery.selectExistsById(classType, values);
    }

    public <T> T selectOne(Class<T> classType, String sql) {
        return dataQuery.selectOne(classType, sql);
    }

    public <T> T selectOne(Class<T> classType, String sql, String k1, Object v1) {
        return dataQuery.selectOne(classType, sql, k1, v1);
    }

    public <T> T selectOne(Class<T> classType, String sql, String k1, Object v1, String k2, Object v2) {
        return dataQuery.selectOne(classType, sql, k1, v1, k2, v2);
    }

    public <T> T selectOne(Class<T> classType, String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return dataQuery.selectOne(classType, sql, k1, v1, k2, v2, k3, v3);
    }

    public <T> PaginationData<T> selectListPagination(Class<T> classType, PaginationQuery query) {
        return dataQuery.selectListPagination(classType, query);
    }

    public <T> PaginationData<T> selectListPagination(Class<T> classType, String sql, Map<String, ?> paramMap, int index, int size) {
        return dataQuery.selectListPagination(classType, sql, paramMap, index, size);
    }

    public <T> PaginationData<T> selectListPagination(PaginationQuery query, RowMapper<T> rowMapper) {
        return dataQuery.selectListPagination(query, rowMapper);
    }

    public Integer selectCount(String sql, Map<String, ?> paramMap) {
        return dataQuery.selectCount(sql, paramMap);
    }

    public SqlDialect getSqlDialect() {
        return dataQuery.getSqlDialect();
    }

    public Integer selectCount(String sql, String k1, Object v1) {
        return dataQuery.selectCount(sql, k1, v1);
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2) {
        return dataQuery.selectCount(sql, k1, v1, k2, v2);
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return dataQuery.selectCount(sql, k1, v1, k2, v2, k3, v3);
    }

    public <T> int insert(T object, KeyHolder keyholder, InsertListener<T> listener) {
        return dataUpdater.insert(object, keyholder, listener);
    }

    public <T> int insert(T object, InsertListener<T> listener) {
        return dataUpdater.insert(object, listener);
    }

    public <T> int insert(T object) {
        return dataUpdater.insert(object);
    }

    public <T> int insert(List<T> objects, InsertListener<List<T>> listener) {
        return dataUpdater.insert(objects, listener);
    }

    public <T> int insert(List<T> objects) {
        return dataUpdater.insert(objects);
    }

    public <T> int update(T object, UpdateListener<T> listener) {
        return dataUpdater.update(object, listener);
    }

    public <T> int update(T object) {
        return dataUpdater.update(object);
    }

    public <T> int update(List<T> objects, UpdateListener<List<T>> listener) {
        return dataUpdater.update(objects, listener);
    }

    public <T> int update(List<T> objects) {
        return dataUpdater.update(objects);
    }

    public <T> int save(T object, InsertListener<T> insertListener, UpdateListener<T> updateListener) {
        return dataUpdater.save(object, insertListener, updateListener);
    }

    public <T> int save(T object) {
        return dataUpdater.save(object);
    }

    public <T> int save(List<T> objects, InsertListener<List<T>> insertListener, UpdateListener<List<T>> updateListener) {
        return dataUpdater.save(objects, insertListener, updateListener);
    }

    public <T> int save(List<T> objects) {
        return dataUpdater.save(objects);
    }

    public <T> int delete(T object, DeleteListener<T> listener) {
        return dataUpdater.delete(object, listener);
    }

    public <T> int delete(T object) {
        return dataUpdater.delete(object);
    }

    public <T> int delete(List<T> objects, DeleteListener<List<T>> listener) {
        return dataUpdater.delete(objects, listener);
    }

    public <T> int delete(List<T> objects) {
        return dataUpdater.delete(objects);
    }

    public <T> int delete(Class<T> tClass) {
        return dataUpdater.delete(tClass);
    }

    public int execute(String sql) {
        return dataUpdater.execute(sql);
    }

    public int execute(String sql, Map<String, ?> paramMap) {
        return dataUpdater.execute(sql, paramMap);
    }
}
