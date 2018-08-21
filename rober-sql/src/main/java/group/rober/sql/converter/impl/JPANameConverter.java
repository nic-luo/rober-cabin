package group.rober.sql.converter.impl;

import group.rober.runtime.kit.ClassKit;
import group.rober.runtime.kit.JpaKit;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.converter.NameConverter;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JPANameConverter<T> implements NameConverter {

    private Map<String, String> columnToFieldMap = new HashMap<String, String>();

    public JPANameConverter(Class<T> mappedClass){
        initialize(mappedClass);
    }

    protected void initialize(Class<T> mappedClass){
        this.columnToFieldMap = new HashMap<String, String>();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(mappedClass);
        for (PropertyDescriptor pd : pds) {
            Method writeMethod = pd.getWriteMethod();
            if (writeMethod != null) {
                String pdName = pd.getName();
//                String upperUnderscore = StringKit.upperUnderscore(pdName);
                String upperUnderscore = StringKit.camelToUnderline(pdName);
                Field field = ClassKit.getField(mappedClass, pdName);
                //如果数据Field不存在，写方法或Field被票房为Transient,则说明此字段需要忽略
                if (field == null) continue;
                if (writeMethod.getAnnotation(Transient.class) != null) continue;
                if (field.getAnnotation(Transient.class) != null) continue;

                String jpaColumn = JpaKit.getColumn(mappedClass, field);
                if (StringKit.isBlank(jpaColumn)) {
                    jpaColumn = upperUnderscore;
                }
                columnToFieldMap.put(jpaColumn, pd.getName());
            }
        }

    }

    public String getPropertyName(String columnName) {
        return columnToFieldMap.get(columnName);
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
