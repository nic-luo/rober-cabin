package group.rober.sql.core.rowmapper;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.MapData;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MapDataUnderLineRowMapper extends MapDataRowMapper {

    public MapData mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        MapData mapOfColValues = createColumnMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
            Object obj = getColumnValue(rs, i);
//            key = StringKit.lowerCamel(key);
            key = StringKit.underlineToCamel(key);
            mapOfColValues.put(key, obj);
        }
        return mapOfColValues;
    }

}
