package group.rober.sql.core;

import group.rober.runtime.kit.*;
import group.rober.sql.core.rowmapper.JpaBeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL到JavaBean之间的查询映射
 */
public class DataQuery extends AbstractQuery {
    public DataQuery() {
    }

    public DataQuery(NamedParameterJdbcTemplate jdbcOperations) {
        this.jdbcTemplate = jdbcOperations;
    }


    protected <T> RowMapper getRowMapper(Class<T> classType){
        RowMapper rowMapper = null;
        if(Number.class.isAssignableFrom(classType)||CharSequence.class.isAssignableFrom(classType)){
            rowMapper = (ResultSet rs, int rowNum)->{
                    if(classType.isAssignableFrom(Integer.class)){
                        return rs.getInt(1);
                    }else if(classType.isAssignableFrom(Long.class)){
                        return rs.getLong(1);
                    }else if(classType.isAssignableFrom(Double.class)){
                        return rs.getDouble(1);
                    }else if(classType.isAssignableFrom(BigDecimal.class)){
                        return rs.getBigDecimal(1);
                    }else{
                        return rs.getString(1);
                    }
                };
        }else{
            rowMapper = super.getRowMapper();
            if(rowMapper==null) rowMapper = new JpaBeanPropertyRowMapper<T>(classType);
        }
        return rowMapper;
    }

    public <T> List<T> selectList(Class<T> classType, String sql, Map<String, ?> parameter){
        long startTime = System.currentTimeMillis();
        List<T> dataList = jdbcTemplate.query(sql,parameter, getRowMapper(classType));
        long endTime = System.currentTimeMillis();
        logSQL("SQL Query",sql,parameter,1,dataList.size(),endTime-startTime);

        return dataList;
    }

    public <T> List<T> selectList(Class<T> classType, String sql){
        return selectList(classType,sql,new HashMap<String, Object>());
    }

    public <T> List<T> selectList(Class<T> classType, String sql,String k1,Object v1){
        return selectList(classType,sql, MapKit.mapOf(k1,v1));
    }

    public <T> List<T> selectList(Class<T> classType, String sql,String k1,Object v1,String k2,Object v2){
        return selectList(classType,sql,MapKit.mapOf(k1,v1,k2,v2));
    }

    public <T> List<T> selectList(Class<T> classType, String sql,String k1,Object v1,String k2,Object v2,String k3,Object v3){
        return selectList(classType,sql,MapKit.mapOf(k1,v1,k2,v2,k3,v3));
    }

    public <T> T selectOne(Class<T> classType, String sql, Map<String, ?> parameter) {
        List<T> dataList = selectList(classType,sql,parameter);
        if(dataList!=null&&dataList.size()>0)return dataList.get(0);
        return null;
    }



    public <T> T selectOneById(Class<T> classType, Map<String,?> paraMap){
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT * FROM ").append(JpaKit.getTableName(classType));
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(JpaKit.getIdWhere(classType));
        String sql = sqlBuffer.toString();

        return selectOne(classType,sql,paraMap);
    }

    private <T,V> Map<String,Object> buildIdValuesToMap(Class<T> classType,V... values){
        Map<String,String> columnPropertyMap = JpaKit.getIdMappedFields(classType);
        Map<String,Object> paramMap = new HashMap<String,Object>();
        int i=0;
        for(Map.Entry<String,String> entry : columnPropertyMap.entrySet()){
            Object v = null;
            if(i<values.length)v = values[i++];
            paramMap.put(entry.getValue(),v);
        }
        return paramMap;
    }

    public <T,V> T selectOneById(Class<T> classType, V... values){
        Map<String,Object> paramMap = buildIdValuesToMap(classType,values);
        return selectOneById(classType,paramMap);
    }

    public <T> boolean selectExistsById(Class<T> classType, Map<String,?> paraMap){
        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append("SELECT count(1) FROM ").append(JpaKit.getTableName(classType));
        sqlBuffer.append(" WHERE ");
        sqlBuffer.append(JpaKit.getIdWhere(classType));
        String sql = sqlBuffer.toString();

        long startTime = System.currentTimeMillis();
        int r = jdbcTemplate.queryForObject(sql, paraMap, new RowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int index) throws SQLException {
                return rs.getInt(1);
            }
        });
        long endTime = System.currentTimeMillis();
        logSQL("SQL Query",sql,paraMap,1,1,endTime-startTime);

        return r > 0;
    }

    public <T,V> boolean selectExistsById(Class<T> classType, V... values){
        Map<String,Object> paramMap = buildIdValuesToMap(classType,values);
        return selectExistsById(classType,paramMap);
    }


    public <T> T selectOne(Class<T> classType, String sql) {
        return selectOne(classType,sql,new HashMap<String, Object>());
    }

    public <T> T selectOne(Class<T> classType, String sql,String k1,Object v1) {
        return selectOne(classType,sql,MapKit.mapOf(k1,v1));
    }

    public <T> T selectOne(Class<T> classType, String sql,String k1,Object v1,String k2,Object v2) {
        return selectOne(classType,sql,MapKit.mapOf(k1,v1,k2,v2));
    }

    public <T> T selectOne(Class<T> classType, String sql,String k1,Object v1,String k2,Object v2,String k3,Object v3) {
        return selectOne(classType,sql,MapKit.mapOf(k1,v1,k2,v2,k3,v3));
    }

    public <T> PaginationData<T> selectListPagination(Class<T> classType, PaginationQuery query){
        return selectListPagination(query, getRowMapper(classType));
    }

    public <T> PaginationData<T> selectListPagination(Class<T> classType, String sql, Map<String,?> paramMap, int index, int size){
        Map<String,Object> queryParamMap = new HashMap<String,Object>();
        queryParamMap.putAll(paramMap);

        PaginationQuery query = new PaginationQuery();
        query.setQuery(sql);
        query.setParameterMap(queryParamMap);
        query.setIndex(index);
        query.setSize(size);

        return selectListPagination(classType,query);
    }

}
