package group.rober.sql.core;

import group.rober.runtime.kit.JSONKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.support.BeanSelfAware;
import group.rober.sql.converter.NameConverter;
import group.rober.sql.converter.impl.UnderlinedNameConverter;
import group.rober.sql.exception.OptimisticLockException;
import group.rober.sql.kit.MapDataSQLKit;
import group.rober.sql.listener.DeleteListener;
import group.rober.sql.listener.InsertListener;
import group.rober.sql.listener.UpdateListener;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapDataUpdater extends AbstractUpdater implements BeanSelfAware{
    private MapDataQuery dataQuery;

    private boolean emptyStringAsNull = true;       //空字串转为空对象
    private String optimisticLockProperty = null;   //乐观锁属性
    //该类实例为单例模式，需要用ThreadLocal来保证线程安全，每一个不同的查询，可以使用到不同的映射规则
//    private ThreadLocal<RowMapper<MapData>> rowMapperThreadLocal = new ThreadLocal<RowMapper<MapData>>();
    private ThreadLocal<NameConverter> nameConverterThreadLocal = new ThreadLocal<NameConverter>();

    public MapDataQuery getDataQuery() {
        return dataQuery;
    }

    public void setDataQuery(MapDataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    public boolean isEmptyStringAsNull() {
        return emptyStringAsNull;
    }

    public void setEmptyStringAsNull(boolean emptyStringAsNull) {
        this.emptyStringAsNull = emptyStringAsNull;
    }

    public String getOptimisticLockProperty() {
        return optimisticLockProperty;
    }

    public void setOptimisticLockProperty(String optimisticLockProperty) {
        this.optimisticLockProperty = optimisticLockProperty;
    }

//    public ThreadLocal<RowMapper<MapData>> getRowMapperThreadLocal() {
//        return rowMapperThreadLocal;
//    }
//
//    public void setRowMapperThreadLocal(ThreadLocal<RowMapper<MapData>> rowMapperThreadLocal) {
//        this.rowMapperThreadLocal = rowMapperThreadLocal;
//    }

    protected NameConverter getNameConverter() {
        NameConverter nameConverter = super.getNameConverter();
        if (nameConverter == null)
            nameConverter = new UnderlinedNameConverter();
        return nameConverter;
    }

    /**
     * 获取代理对象自己，用于方法内部自我调用，无法AOP问题
     */
    @Resource(name ="mapDataUpdater")
    private MapDataUpdater self;

    public void setSelf(BeanSelfAware self) {
        if(self instanceof MapDataUpdater) {
            this.self = (MapDataUpdater) self;
        }
    }
    public MapDataUpdater self() {
        return self;
    }

    public int insert(String table, MapData entity, KeyHolder keyholder, InsertListener<MapData> listener){
        if(listener != null) listener.before(entity);
        if(emptyStringAsNull) MapDataSQLKit.emptyStringAsNull(entity);  //空串转为空（NULL)

        String sql = MapDataSQLKit.genInsertSql(table,entity,optimisticLockProperty,getNameConverter());
        SqlParameterSource ps=new MapSqlParameterSource(entity);

        //执行
        long startTime = System.currentTimeMillis();
        int r = jdbcTemplate.update(sql,ps,keyholder);
        long endTime = System.currentTimeMillis();
        logSQL("Insert",sql,entity,1,r,endTime-startTime);

        if(listener != null) listener.after(entity);
        return r;
    }

    public int insert(String table, MapData entity, InsertListener<MapData> listener){
        return insert(table,entity,new GeneratedKeyHolder(),listener);
    }

    public int insert(String table,MapData entity){
        return insert(table,entity,new GeneratedKeyHolder(),null);
    }

    public int insert(String table, List<MapData> entityList, InsertListener<List<MapData>> listener){
        if(listener != null) listener.before(entityList);
        ValidateKit.notEmpty(entityList,"要更新的数据列表不能为空");

        int ret = 0;

        if(entityList.size()==1){
            insert(table,entityList.get(0));
        }else{
            String sql = null;
            SqlParameterSource[] batchArgs = new SqlParameterSource[entityList.size()];
            for(int i=0;i<entityList.size() ;i++){
                MapData entity = entityList.get(i);
                if(sql==null)sql = MapDataSQLKit.genInsertSql(table,entity,optimisticLockProperty,getNameConverter());
                if(emptyStringAsNull) MapDataSQLKit.emptyStringAsNull(entity);  //空串转为空（NULL)
                batchArgs[i]=new MapSqlParameterSource(entity);
            }
            //执行
            long startTime = System.currentTimeMillis();
            int[] r = jdbcTemplate.batchUpdate(sql,batchArgs);
            for(int i=0;i<r.length;i++){
                ret += r[i];
            }
            long endTime = System.currentTimeMillis();
            logSQL("Batch Insert",sql,entityList,entityList.size(),ret,endTime-startTime);
        }

        if(listener != null) listener.after(entityList);
        return ret;
    }

    public int insert(String table,List<MapData> entityList){
        return insert(table,entityList,null);
    }

    public int update(String table, MapData entity, MapData keyAttributes, UpdateListener<MapData> listener){
        if(listener != null) listener.before(entity);
        if(emptyStringAsNull) MapDataSQLKit.emptyStringAsNull(entity);  //空串转为空（NULL)

        String sql = MapDataSQLKit.genUpdateSql(table,entity,keyAttributes,optimisticLockProperty,getNameConverter());
        MapSqlParameterSource ps=new MapSqlParameterSource(entity);
        ps.addValues(keyAttributes);
        //执行
        long startTime = System.currentTimeMillis();
        int ret = jdbcTemplate.update(sql,ps);
        //使用了乐观锁，但是更新不到数据的情况下
        if(StringKit.isNotBlank(optimisticLockProperty)&& ret==0){
            throw new OptimisticLockException("SQL[{0}],Parameter:{1}",sql, JSONKit.toJsonString(entity));
        }
        long endTime = System.currentTimeMillis();
        logSQL("Update",sql,entity,1,ret,endTime-startTime);

        if(listener != null) listener.after(entity);
        return ret;
    }
    public int update(String table, MapData entity, MapData keyAttributes){
        return update(table,entity,keyAttributes,null);
    }

    public int update(String table, List<MapData> entityList, List<MapData> keyAttributesList, UpdateListener<List<MapData>> listener){
        if(listener != null) listener.before(entityList);
         ValidateKit.notEmpty(entityList,"update data list cannot be empty");
        ValidateKit.notEmpty(keyAttributesList,"update key attributes cannot be empty");

        int ret = 0;

        if(entityList.size()==1){
            update(table,entityList.get(0),keyAttributesList.get(0));
        }else{
            String sql = null;
            SqlParameterSource[] batchArgs = new SqlParameterSource[entityList.size()];
            for(int i=0;i<entityList.size() ;i++){
                Map<String,Object> entity = entityList.get(i);
                if(sql==null)sql = MapDataSQLKit.genUpdateSql(table,entity,keyAttributesList.get(i),optimisticLockProperty,getNameConverter());
                if(emptyStringAsNull) MapDataSQLKit.emptyStringAsNull(entity);  //空串转为空（NULL)
                batchArgs[i]=new MapSqlParameterSource(entity);
            }

            long startTime = System.currentTimeMillis();
            int[] r = jdbcTemplate.batchUpdate(sql,batchArgs);
            for(int i=0;i<r.length;i++){
                ret += r[i];
                if(r[i]==0){
                    throw new OptimisticLockException("SQL[{0}],Parameter:{1}",sql, JSONKit.toJsonString(entityList.get(i)));
                }
            }
            long endTime = System.currentTimeMillis();
            logSQL("Batch Update",sql,entityList,entityList.size(),ret,endTime-startTime);
        }

        if(listener != null) listener.after(entityList);
        return ret;
    }

    public int update(String table, List<MapData> entity, List<MapData> keyAttributes){
        return update(table,entity,keyAttributes,null);
    }

    public int save(String table, MapData entity, MapData keyAttributes, KeyHolder keyholder, InsertListener<MapData> insertListener, UpdateListener<MapData> updateListener){
        boolean exists = MapDataSQLKit.exists(jdbcTemplate,table,keyAttributes,getNameConverter());
        if(exists){
            return self.update(table,entity,keyAttributes,updateListener);
        }else{
            return self.insert(table,entity,keyholder,insertListener);
        }
    }

    public int save(String table, MapData entity, MapData keyAttributes, KeyHolder keyHolder){
        return save(table,entity,keyAttributes,keyHolder,null,null);
    }

    public int save(String table, MapData entity, MapData keyAttributes){
        return save(table,entity,keyAttributes,new GeneratedKeyHolder());
    }

    public int save(String table, List<MapData> entityList, List<MapData> keyAttributesList, InsertListener<List<MapData>> insertListener, UpdateListener<List<MapData>> updateListener){
        ValidateKit.notEmpty(entityList,"update data list cannot be empty");
        ValidateKit.notEmpty(keyAttributesList,"update key attributes cannot be empty");
        ValidateKit.isTrue(entityList.size()==keyAttributesList.size(),"data list length must be equal to parameter list length");

        List<Integer> updatedIndexList = new ArrayList<Integer>();
        for(int i=0;i<keyAttributesList.size();i++){
            boolean exists = MapDataSQLKit.exists(jdbcTemplate,table,keyAttributesList.get(i),getNameConverter());
            if(exists)updatedIndexList.add(i);
        }

        List<MapData> insertDataList = new ArrayList<MapData>();
        List<MapData> updateDataList = new ArrayList<MapData>();
        List<MapData> updateKeyList = new ArrayList<MapData>();

        //把需要插入的部分和更新的部分分出来
        for(int i=0;i<entityList.size();i++){
            if(updatedIndexList.indexOf(i)>=0){
                updateDataList.add(entityList.get(i));
                updateKeyList.add(keyAttributesList.get(i));
            }else{
                insertDataList.add(entityList.get(i));
            }
        }

        int ret = 0;
        if(insertDataList.size()>0){
            ret += self.insert(table,insertDataList,insertListener);
        }
        if(updateDataList.size()>0){
            ret += self.update(table,updateDataList,updateKeyList,updateListener);
        }

        return ret;
    }

    public int save(String table, List<MapData> entityList, List<MapData> keyAttributesList){
        return save(table,entityList,keyAttributesList,null,null);
    }

    public int delete(String table, MapData keyAttributes){
        return delete(table,keyAttributes,null);
    }

    public int delete(String table, MapData keyAttributes, DeleteListener<MapData> listener){
        if(listener!=null)listener.before(keyAttributes);

        String sql = MapDataSQLKit.genDeleteSql(table,keyAttributes,getOptimisticLockProperty(),getNameConverter());
        int r = execute(sql,keyAttributes);

        if(listener!=null)listener.after(keyAttributes);

        return r;
    }

    public int delete(String table,List<MapData> keyAttributesList){
        return delete(table,keyAttributesList,null);
    }

    public int delete(String table,List<MapData> keyAttributesList,DeleteListener<List<MapData>> listener){
        if(listener!=null)listener.before(keyAttributesList);

        int r = 0;
        for(MapData row : keyAttributesList){
            r += delete(table,row,null);
        }

        if(listener!=null)listener.after(keyAttributesList);

        return r;
    }

}
