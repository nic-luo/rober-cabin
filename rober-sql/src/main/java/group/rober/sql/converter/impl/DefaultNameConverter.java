package group.rober.sql.converter.impl;

import group.rober.sql.converter.NameConverter;

public class DefaultNameConverter implements NameConverter {
    public String getPropertyName(String columnName) {
        return columnName;
    }

    public String getColumnName(String propertyName) {
        return propertyName;
    }

    public String getClassName(String tableName) {
        return tableName;
    }

    public String getTableName(Class<?> clazz) {
        return clazz.getSimpleName();
    }
}
