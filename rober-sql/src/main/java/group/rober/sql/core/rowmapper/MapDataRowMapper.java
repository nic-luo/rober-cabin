package group.rober.sql.core.rowmapper;

import group.rober.runtime.lang.MapData;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class MapDataRowMapper implements RowMapper<MapData> {

    public MapData mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        MapData mapOfColValues = createColumnMap(columnCount);
        for (int i = 1; i <= columnCount; i++) {
            String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
            Object obj = getColumnValue(rs, i);
            mapOfColValues.put(key, obj);
        }
        return mapOfColValues;
    }

    protected MapData createColumnMap(int columnCount) {
        return new MapData(columnCount);
//        return new LinkedCaseInsensitiveMap<Object>(columnCount);
    }

    protected String getColumnKey(String columnName) {
        return columnName;
    }

    protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index);
    }

}
