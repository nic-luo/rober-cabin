package group.rober.sql.converter.impl;

import group.rober.runtime.kit.StringKit;
import group.rober.sql.converter.NameConverter;

public class UnderlinedNameConverter implements NameConverter {

    public String getPropertyName(String columnName) {
        if(columnName==null)return columnName;
//        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
        return StringKit.underlineToCamel(columnName);
    }

    public String getColumnName(String propertyName) {
        if(propertyName==null)return propertyName;
//        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, propertyName);
        return StringKit.camelToUnderline(propertyName);
    }

    public String getClassName(String tableName) {
        if(tableName==null)return tableName;
//        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, tableName);
        return StringKit.underlineToCamel(tableName);
    }

    public String getTableName(Class<?> clazz) {
        if(clazz==null)return null;
        String name = clazz.getSimpleName();
//        return CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
        return StringKit.camelToUnderline(name);

    }
}
