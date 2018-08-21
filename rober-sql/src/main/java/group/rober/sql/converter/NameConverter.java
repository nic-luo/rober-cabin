package group.rober.sql.converter;

/**
 * 名称转换器,字段名和属性名,表名和类名之间相互转换
 */
public interface NameConverter {
    /**
     * 把数据表列名转换为字段属性名
     *
     * @param columnName columnName
     * @return String
     */
    String getPropertyName(String columnName);

    /**
     * 把属性名转换为数据表字段名
     *
     * @param propertyName propertyName
     * @return String
     */
    String getColumnName(String propertyName);

    /**
     * 根据表名转换为类名
     *
     * @param tableName tableName
     * @return String
     */
    String getClassName(String tableName);

    /**
     * 把类转换为表名
     *
     * @param clazz clazz
     * @return String
     */
    String getTableName(Class<?> clazz);

}
