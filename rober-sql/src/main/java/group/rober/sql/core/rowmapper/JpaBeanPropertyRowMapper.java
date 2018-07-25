package group.rober.sql.core.rowmapper;

import group.rober.runtime.kit.ClassKit;
import group.rober.runtime.kit.JpaKit;
import group.rober.runtime.kit.StringKit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;

import javax.persistence.Transient;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 参考BeanPropertyRowMapper
 *
 * @param <T>
 */
public class JpaBeanPropertyRowMapper<T> implements RowMapper<T> {

    protected final Log logger = LogFactory.getLog(getClass());

    private ConversionService conversionService = DefaultConversionService.getSharedInstance();

    private Class<T> mappedClass;
    private Map<PropertyDescriptor, String> filedToColumnMap;


    public JpaBeanPropertyRowMapper() {
    }

    public JpaBeanPropertyRowMapper(Class<T> mappedClass) {
        initialize(mappedClass);
    }

    public JpaBeanPropertyRowMapper(Class<T> mappedClass, boolean checkFullyPopulated) {
        initialize(mappedClass);
    }


    public void setMappedClass(Class<T> mappedClass) {
        if (this.mappedClass == null) {
            initialize(mappedClass);
        } else {
            if (this.mappedClass != mappedClass) {
                throw new InvalidDataAccessApiUsageException("The mapped class can not be reassigned to map to " +
                        mappedClass + " since it is already providing mapping for " + this.mappedClass);
            }
        }
    }

    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    protected void initialize(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
        this.filedToColumnMap = new HashMap<PropertyDescriptor, String>();
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
                filedToColumnMap.put(pd, jpaColumn);
            }
        }
    }

    private Map<String, Integer> columnIndexMap = new HashMap<String, Integer>();

    private int lookupColumnIndex(ResultSetMetaData resultSetMetaData, String column) throws SQLException {
        if (columnIndexMap.containsKey(column)) return columnIndexMap.get(column);
        int columnCount = resultSetMetaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            if (column.equalsIgnoreCase(resultSetMetaData.getColumnLabel(i))) {
                columnIndexMap.put(column, i);
                return i;
            }
            if (column.equalsIgnoreCase(resultSetMetaData.getColumnName(i))) {
                columnIndexMap.put(column, i);
                return i;
            }
        }
        return 0;
    }

    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        initBeanWrapper(bw);

        ResultSetMetaData rsmd = rs.getMetaData();

        Iterator<PropertyDescriptor> iterator = filedToColumnMap.keySet().iterator();
        while (iterator.hasNext()) {
            PropertyDescriptor pd = iterator.next();
            String columnName = filedToColumnMap.get(pd);
            int index = lookupColumnIndex(rsmd, columnName);
            if (index <= 0) continue;
            Object value = JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
            if (value == null) continue;
            bw.setPropertyValue(pd.getName(), value);
        }

        return mappedObject;
    }

    public ConversionService getConversionService() {
        return conversionService;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }
    }

    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }


    public static <T> BeanPropertyRowMapper<T> newInstance(Class<T> mappedClass) {
        return new BeanPropertyRowMapper<T>(mappedClass);
    }

}
