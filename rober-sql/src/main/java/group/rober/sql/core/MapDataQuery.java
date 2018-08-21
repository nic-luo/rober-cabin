package group.rober.sql.core;

import group.rober.runtime.kit.MapKit;
import group.rober.runtime.lang.MapData;
import group.rober.sql.converter.NameConverter;
import group.rober.sql.core.rowmapper.MapDataRowMapper;
import group.rober.sql.core.rowmapper.MapDataUnderLineRowMapper;
import org.springframework.jdbc.core.RowMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 针对MapData的数据查询处理
 */
public class MapDataQuery extends AbstractQuery{
    private static MapDataRowMapper defaultRowMapper = new MapDataUnderLineRowMapper();
    private ThreadLocal<RowMapper> rowMapperHolder = new ThreadLocal<RowMapper>();

    public MapDataQuery() {
    }

    public List<MapData> selectList(String sql, Map<String, ?> parameter){
        long startTime = System.currentTimeMillis();
        List<MapData> dataList = jdbcTemplate.query(sql,parameter,getRowMapper());
        long endTime = System.currentTimeMillis();
        logSQL("SQL Query",sql,parameter,1,dataList.size(),endTime-startTime);

        return dataList;
    }
    public List<MapData> selectList(String sql){
        return selectList(sql,new HashMap<String, Object>());
    }

    public List<MapData> selectList(String sql,String k1,Object v1){
        return selectList(sql, MapKit.mapOf(k1,v1));
    }

    public List<MapData> selectList(String sql,String k1,Object v1,String k2,Object v2){
        return selectList(sql,MapKit.mapOf(k1,v1,k2,v2));
    }

    public List<MapData> selectList(String sql,String k1,Object v1,String k2,Object v2,String k3,Object v3){
        return selectList(sql,MapKit.mapOf(k1,v1,k2,v2,k3,v3));
    }

    public MapData selectOne(String sql, Map<String, ?> parameter) {
        List<MapData> dataList = selectList(sql,parameter);
        if(dataList!=null&&dataList.size()>0)return dataList.get(0);
        return null;
    }

    public MapData selectOne(String sql) {
        return selectOne(sql,new HashMap<String, Object>());
    }

    public MapData selectOne(String sql,String k1,Object v1) {
        return selectOne(sql,MapKit.mapOf(k1,v1));
    }

    public MapData selectOne(String sql,String k1,Object v1,String k2,Object v2) {
        return selectOne(sql,MapKit.mapOf(k1,v1,k2,v2));
    }

    public MapData selectOne(String sql,String k1,Object v1,String k2,Object v2,String k3,Object v3) {
        return selectOne(sql,MapKit.mapOf(k1,v1,k2,v2,k3,v3));
    }

    public PaginationData<MapData> selectListPagination(PaginationQuery query){
        return selectListPagination(query,getRowMapper());
    }

    public PaginationData<MapData> selectListPagination(String sql, Map<String,?> paramMap, int index, int size){
        Map<String,Object> queryParamMap = new HashMap<String,Object>();
        queryParamMap.putAll(paramMap);

        PaginationQuery query = new PaginationQuery();
        query.setQuery(sql);
        query.setParameterMap(queryParamMap);
        query.setIndex(index);
        query.setSize(size);

        return selectListPagination(query);
    }

    public RowMapper getRowMapper() {
        RowMapper rowMapper = super.getRowMapper();
        if(rowMapper==null)rowMapper = defaultRowMapper;
        return rowMapper;
    }

}
