package group.rober.sql.kit;

import group.rober.runtime.kit.JpaKit;
import group.rober.runtime.kit.ListKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class DataSQLKit {


    /**
     * 取JavaBean主键，KEY=数据库字段，VALUE=JavaBean属性名
     *
     * @param classType classType
     * @return map
     */
    public Map<String,String> getKeyMaps(Class<?> classType){
        Map<String,String> keyMap = new HashMap<String,String>();
        Map<String,String> mappedFields = JpaKit.getMappedFields(classType);
        
        List<Field> idFields = JpaKit.getIdFields(classType);
        ValidateKit.notEmpty(idFields,"类{0}不存在@Id注解",classType.getName());
        for(Field field : idFields){
//            keyList.add(field.getName()+"=:"+field.getName());
        }

        return keyMap;
    }

    public static String getExistsSqlByKey(Class<?> classType) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" SELECT count(1) FROM ");
        sqlBuilder.append(JpaKit.getTableName(classType));
        sqlBuilder.append(" WHERE ");
        List<Field> idFields = JpaKit.getIdFields(classType);
        ValidateKit.notEmpty(idFields,"类{0}不存在@Id注解",classType.getName());
        List<String> whereItems = new ArrayList<String>();
        for(Field field : idFields){
            whereItems.add(field.getName()+"=:"+field.getName());
        }
        sqlBuilder.append(StringKit.join(whereItems," AND "));
        String sql = sqlBuilder.toString();
        return sql;
    }

    public static String getDeleteSqlByKey(Class<?> classType) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" DELETE FROM ");
        sqlBuilder.append(JpaKit.getTableName(classType));
        sqlBuilder.append(" WHERE ");
        sqlBuilder.append(JpaKit.getIdWhere(classType));
        String sql = sqlBuilder.toString();
        return sql;
    }

    public static String getDeleteAllSql(Class<?> classType) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" DELETE FROM ");
        sqlBuilder.append(JpaKit.getTableName(classType));
        return sqlBuilder.toString();
    }

    public static String getInsertSql(Class<?> classType){
        Map<String,String> mappedFields = JpaKit.getMappedFields(classType);
        List<String> columns = ListKit.newArrayList();
        List<String> fields = ListKit.newArrayList();
        for(Map.Entry<String,String> entry : mappedFields.entrySet()){
            columns.add(entry.getKey());
            fields.add(":"+entry.getValue());
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" INSERT INTO ");
        sqlBuilder.append(JpaKit.getTableName(classType));
        sqlBuilder.append("(");
        sqlBuilder.append(StringKit.join(columns,","));
        sqlBuilder.append(") ");
        sqlBuilder.append(" VALUES");
        sqlBuilder.append("(");
        sqlBuilder.append(StringKit.join(fields,","));
        sqlBuilder.append(")");

        return sqlBuilder.toString();
    }

    public static String getUpdateSql(Class<?> classType){
        Map<String,String> mappedFields = JpaKit.getMappedFields(classType);
        Map<String,String> idMap = JpaKit.getIdMappedFields(classType);
        ValidateKit.notEmpty(idMap,"类[{0}]不存在@Id注解",classType.getName());

        List<String> setItems = ListKit.newArrayList();
        List<String> whereItems = ListKit.newArrayList();
        for(Map.Entry<String,String> entry : mappedFields.entrySet()){
            setItems.add(entry.getKey()+"=:"+entry.getValue());
        }
        for(Map.Entry<String,String> entry : idMap.entrySet()){
            whereItems.add(entry.getKey()+"=:"+entry.getValue());
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(" UPDATE ");
        sqlBuilder.append(JpaKit.getTableName(classType));
        sqlBuilder.append(" SET ");
        sqlBuilder.append(StringKit.join(setItems,","));
        sqlBuilder.append(" WHERE ");
        sqlBuilder.append(StringKit.join(whereItems," AND "));

        return sqlBuilder.toString();
    }
}
