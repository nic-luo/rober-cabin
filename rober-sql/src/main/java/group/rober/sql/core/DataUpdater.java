package group.rober.sql.core;

import group.rober.runtime.kit.BeanKit;
import group.rober.runtime.kit.JpaKit;
import group.rober.runtime.kit.ListKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.support.BeanSelfAware;
import group.rober.sql.kit.DataSQLKit;
import group.rober.sql.listener.DeleteListener;
import group.rober.sql.listener.InsertListener;
import group.rober.sql.listener.UpdateListener;
import group.rober.sql.serialno.finder.SerialNoGeneratorFinder;
import group.rober.sql.serialno.generator.SerialNoGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Resource;
import javax.persistence.GeneratedValue;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL到JavaBean之间的写入映射
 */
public class DataUpdater extends AbstractUpdater implements BeanSelfAware  {
    private DataQuery dataQuery;
    private SerialNoGeneratorFinder serialNoGeneratorFinder;
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取代理对象自己，用于方法内部自我调用，无法AOP问题
     */
    @Resource(name ="dataUpdater")
    private DataUpdater self;


    public void setSelf(BeanSelfAware self) {
        if(self instanceof DataUpdater) {
            this.self = (DataUpdater) self;
        }
    }
    public BeanSelfAware self() {
        return self;
    }

    public DataQuery setDataQuery() {
        return dataQuery;
    }

    public void setDataQuery(DataQuery dataQuery) {
        this.dataQuery = dataQuery;
    }

    public void setSerialNoGeneratorFinder(SerialNoGeneratorFinder finder) {
        this.serialNoGeneratorFinder = finder;
    }

    private void fillSerialNo(Object object) {
        List<Field> fields = JpaKit.getGeneratedValueFields(object.getClass());
        for (Field field : fields) {
            Object fieldValue = BeanKit.getPropertyValue(object, field.getName());
            if (fieldValue == null || StringKit.isBlank(fieldValue.toString())) {
                GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
                String generatorId = StringKit.isBlank(gv.generator()) ?
                    object.getClass().getName() + "." + field.getName() :
                    gv.generator();
                SerialNoGenerator<String> serialNoGenerator =
                    serialNoGeneratorFinder.find(generatorId);
                String serialNo = serialNoGenerator.next(generatorId, object, null);
                BeanKit.setPropertyValue(object, field.getName(), serialNo);
            }
        }
    }

    private <T> void fillSerialNos(List<T> objects) {
        Object object = objects.get(0);
        List<Field> fields = JpaKit.getGeneratedValueFields(object.getClass());
        Map<String, String[]> serialNoMap = new HashMap();
        for (Field field : fields) {
            Object fieldValue = BeanKit.getPropertyValue(object, field.getName());
            if (fieldValue == null || StringKit.isBlank(fieldValue.toString())) {
                GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
                String generatorId = StringKit.isBlank(gv.generator()) ?
                        object.getClass().getName() + "." + field.getName() :
                        gv.generator();
                SerialNoGenerator<String> serialNoGenerator =
                        serialNoGeneratorFinder.find(generatorId);
                String[] serialNos = serialNoGenerator.nextBatch(generatorId, objects.size());
                serialNoMap.put(field.getName(), serialNos);
            }
        }
        for (int i = 0; i < objects.size(); i++) {
            for (Field field : fields) {
                Object fieldValue = BeanKit.getPropertyValue(objects.get(i), field.getName());
                if (fieldValue == null || StringKit.isBlank(fieldValue.toString())) {
                    BeanKit.setPropertyValue(objects.get(i), field.getName(), serialNoMap.get(field.getName())[i]);
                }
            }
        }
    }

    public <T> int insert(T object, KeyHolder keyholder, InsertListener<T> listener){
        if(listener != null) listener.before(object);

        String sql = DataSQLKit.getInsertSql(object.getClass());
        fillSerialNo(object);
        SqlParameterSource sps = new BeanPropertySqlParameterSource(object);

        //执行
        long startTime = System.currentTimeMillis();
        int r = jdbcTemplate.update(sql,sps,keyholder);
        long endTime = System.currentTimeMillis();
        logSQL("Insert",sql,object,1,r,endTime-startTime);

        if(listener != null)listener.after(object);

        return r;
    }
    public <T> int insert(T object, InsertListener<T> listener){
        return insert(object,new GeneratedKeyHolder(),listener);
    }
    public <T> int insert(T object){
        return insert(object,null);
    }

    public <T> int insert(List<T> objects, InsertListener<List<T>> listener){
        if(objects==null||objects.size()==0)return 0;
//        ValidateKit.notEmpty(objects,"要插入的数据列表不能为空");
        if(listener != null) listener.before(objects);

        int ret = 0;

        if(objects.size()==1){
            insert(objects.get(0));
        }else{
            String sql = DataSQLKit.getInsertSql(objects.get(0).getClass());
            fillSerialNos(objects);
            SqlParameterSource[] spsList = new SqlParameterSource[objects.size()];
            for(int i=0;i<objects.size();i++){
                spsList[i] = new BeanPropertySqlParameterSource(objects.get(i));
            }
            //执行
            long startTime = System.currentTimeMillis();
            int[] r = jdbcTemplate.batchUpdate(sql,spsList);
            long endTime = System.currentTimeMillis();

            for(int i=0;i<r.length;i++){
                ret += r[i];
            }
            logSQL("Batch Insert",sql,objects,objects.size(),ret,endTime-startTime);
        }

        if(listener != null)listener.after(objects);
        return ret;
    }
    public <T> int insert(List<T> objects){
        return insert(objects,null);
    }

    public <T> int update(T object,UpdateListener<T> listener){
        if(listener != null )listener.before(object);

        String sql = DataSQLKit.getUpdateSql(object.getClass());
        SqlParameterSource sps = new BeanPropertySqlParameterSource(object);

        long startTime = System.currentTimeMillis();
        int ret = jdbcTemplate.update(sql,sps);
        long endTime = System.currentTimeMillis();

        logSQL("Update",sql,object,1,ret,endTime-startTime);

        if(listener != null) listener.after(object);

        return ret;
    }

    public <T> int update(T object){
        return update(object,null);
    }
    public <T> int update(List<T> objects, UpdateListener<List<T>> listener){
        if(objects==null||objects.size()==0)return 0;
        if(listener != null) listener.before(objects);

        int ret = 0;

        if(objects.size()==1){
            update(objects.get(0));
        }else{
            String sql = DataSQLKit.getUpdateSql(objects.get(0).getClass());

            SqlParameterSource[] spsList = new SqlParameterSource[objects.size()];
            for(int i=0;i<objects.size();i++){
                spsList[i] = new BeanPropertySqlParameterSource(objects.get(i));
            }
            //执行
            long startTime = System.currentTimeMillis();
            int[] r = jdbcTemplate.batchUpdate(sql,spsList);
            long endTime = System.currentTimeMillis();

            for(int i=0;i<r.length;i++){
                ret += r[i];
            }

            logSQL("Batch Update",sql,objects,objects.size(),ret,endTime-startTime);
        }

        if(listener != null)listener.after(objects);
        return ret;
    }
    public <T> int update(List<T> objects){
        return update(objects,null);
    }

    /**
     * 根据业务主键保存，自动判别是插入还是更新
     *
     * @param object
     * @param insertListener
     * @param updateListener
     * @param <T>
     * @return
     */
    public <T> int save(T object, InsertListener<T> insertListener, UpdateListener<T> updateListener){
        Map<String,Object> idMap = JpaKit.getIdMap(object);
        ValidateKit.notEmpty(idMap,"类[{0}]不存在@Id注解",object.getClass().getName());
        boolean exists = dataQuery.selectExistsById(object.getClass(),idMap);
        if(!exists){
            return self.insert(object,insertListener);
//            return insert(object,insertListener);
        }else{
            return self.update(object,updateListener);
//            return update(object,updateListener);
        }
    }
    public <T> int save(T object){
        return save(object,null,null);
    }

    /**
     * 根据业务主键保存，自动判别是插入还是更新
     *
     * @param objects
     * @param insertListener
     * @param updateListener
     * @param <T>
     * @return
     */
    public <T> int save(List<T> objects,InsertListener<List<T>> insertListener, UpdateListener<List<T>> updateListener){
        List<T> insertDataList = ListKit.newArrayList();
        List<T> updateDataList = ListKit.newArrayList();

        for(T object : objects){
            Map<String,Object> idMap = JpaKit.getIdMap(object);
            boolean exists = dataQuery.selectExistsById(object.getClass(),idMap);
            if(exists){
                updateDataList.add(object);
            }else{
                insertDataList.add(object);
            }
        }

        int ret = 0;
        if(insertDataList.size()>0){
            ret += self.insert(insertDataList,insertListener);
        }
        if(updateDataList.size()>0){
            ret += self.update(updateDataList,updateListener);
        }

        return ret;
    }

    public <T> int save(List<T> objects){
        return save(objects,null,null);
    }

    public <T> int delete(T object, DeleteListener<T> listener){
        if(listener != null )listener.before(object);

        String sql = DataSQLKit.getDeleteSqlByKey(object.getClass());
        SqlParameterSource sps = new BeanPropertySqlParameterSource(object);
        long startTime = System.currentTimeMillis();
        int ret = jdbcTemplate.update(sql,sps);
        long endTime = System.currentTimeMillis();

        logSQL("Batch Delete",sql,object,1,ret,endTime-startTime);

        if(listener != null) listener.after(object);

        return ret;
    }

    public <T> int delete(T object){
        return delete(object,null);
    }

    public <T> int delete(List<T> objects,DeleteListener<List<T>> listener){
        if(listener != null )listener.before(objects);

        String sql = DataSQLKit.getDeleteSqlByKey(objects.get(0).getClass());

        SqlParameterSource[] spsList = new SqlParameterSource[objects.size()];
        for(int i=0;i<objects.size();i++){
            spsList[i] = new BeanPropertySqlParameterSource(objects.get(i));
        }
        //执行
        int ret = 0;
        long startTime = System.currentTimeMillis();
        int[] r = jdbcTemplate.batchUpdate(sql,spsList);
        long endTime = System.currentTimeMillis();
        for(int i=0;i<r.length;i++){
            ret += r[i];
        }

        logSQL("Delete",sql,objects,objects.size(),ret,endTime-startTime);

        if(listener != null) listener.after(objects);

        return ret;
    }

    public <T> int delete(List<T> objects){
        return delete(objects,null);
    }

    public <T> int delete(Class<T> tClass) {
        String sql = DataSQLKit.getDeleteAllSql(tClass);

        //执行
        int ret = 0;
        long startTime = System.currentTimeMillis();
        int[] r = jdbcTemplate.batchUpdate(sql, new SqlParameterSource[0]);
        long endTime = System.currentTimeMillis();
        for(int i=0;i<r.length;i++){
            ret += r[i];
        }
        logSQL("Delete",sql, null, 0, ret, endTime - startTime);
        return ret;
    }

}
