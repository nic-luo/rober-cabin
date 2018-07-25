package group.rober.runtime.kit;

//import group.rober.runtime.jdbc.NameConverter;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.RoberException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by tisir yangsong158@qq.com on 2017-02-19
 */
public abstract class SQLKit {
    public static List<String> LOG_SQL_CTX_PARAM_LIST;

    private static Pattern SINGLE_QUOTE_PATTERN = Pattern.compile("(^'\\w*'$)|^\\d+|^NULL$",Pattern.CASE_INSENSITIVE);
    /**
     * 是否为常数列，例如 '','123' as Name，80 as NUMBER_RESULT等
     *
     * @param column column
     * @return boolean
     */
    public static boolean isConstColumn(String column){
        String col = StringKit.nvl(column,"").trim();
        return SINGLE_QUOTE_PATTERN.matcher(col).find();
    }
    /**
     * SQL的日期转JAVA日期
     *
     * @param sqlDate sqlDate
     * @return date
     */
    public static Date javaDate(java.sql.Date sqlDate){
        if(sqlDate==null)return null;
        Date date = new Date (sqlDate.getTime());
        return date;
    }
    public static Date javaDate(java.sql.Time time){
        if(time==null)return null;
        Date date = new Date (time.getTime());
        return date;
    }
    public static Date javaDate(java.sql.Timestamp time){
        if(time==null)return null;
        Date date = new Date (time.getTime());
        return date;
    }

    /**
     * JAVA日期转SQL日期
     *
     * @param javaDate javaDate
     * @return date
     */
    public static java.sql.Date sqlDate(Date javaDate){
        if(javaDate==null)return null;
        return new java.sql.Date(javaDate.getTime());
    }

    public static String replaceSQLInjector(String str){
        String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";
        return str.replaceAll(reg, "");
    }

//    public static MapData getRowValue(ResultSet rs, Map<Integer,String> columnPropertyMap) throws SQLException{
//        MapData rowObject = new MapData();
//
//        ResultSetMetaData metaData = rs.getMetaData();
//        int columnCount = metaData.getColumnCount();
//        for(int i=1;i<=columnCount;i++){
//            Object value = JdbcUtils.getResultSetValue(rs,i);
//            String propertyName = columnPropertyMap.get(i);
//            if(StringKit.isBlank(propertyName))continue;
//            rowObject.put(propertyName,value);
//        }
//
//        return rowObject;
//    }

//    public static DataObject getRowValue(ResultSet rs, NameConverter converter) throws SQLException {
//        DataObject row = new DataObject();
//
//        ResultSetMetaData meta = rs.getMetaData();
//        int cols = meta.getColumnCount();
//        for(int i=1;i<=cols;i++){
//            String columnName = meta.getColumnName(i);
//            Object value = JdbcUtils.getResultSetValue(rs,i);;
//            //从两个方向取
//            String propName = converter.getPropertyName(columnName);
//            if(StringKit.isBlank(propName))propName = converter.getPropertyName(i);
//
//            row.put(propName,value);
//        }
//
//        return row;
//    }

    public static List<MapData> getTableMeta(Connection connection,String table){
        List<MapData> retList = ListKit.newArrayList();
        try {
            DatabaseMetaData meta = connection.getMetaData();
            String schema = getSchema(connection);
            ResultSet resultSet = meta.getTables(null, "%", table, new String[] { "TABLE" });

            while (resultSet.next()) {
                String tableName=resultSet.getString("TABLE_NAME");

                if(tableName.equals(table)){
                    ResultSet rs = connection.getMetaData().getColumns(null,schema,tableName.toUpperCase(), "%");

                    while(rs.next()){
                        //System.out.println("字段名："+rs.getString("COLUMN_NAME")+"--字段注释："+rs.getString("REMARKS")+"--字段数据类型："+rs.getString("TYPE_NAME"));
                        MapData row = new MapData();
                        String columnName = rs.getString("COLUMN_NAME");
                        String comment = StringKit.nvl(rs.getString("REMARKS"),columnName);
                        int dataType = rs.getInt("DATA_TYPE");
//                        String dbType = rs.getString("TYPE_NAME");

                        row.put("columnName", columnName);
                        row.put("comment",comment);
                        row.put("dataType",dataType);

                        retList.add(row);
                    }
                }
            }

            IOKit.close(resultSet);
        } catch (SQLException e) {
            throw new RoberException(e);
        } finally {

        }

        return retList;
    }

    private static String getSchema(Connection conn) throws SQLException {
        String schema = conn.getMetaData().getUserName();
        if (StringKit.isBlank(schema)) {
            throw new RoberException("数据库schema没获取到");
        }
        return schema.toUpperCase().toString();
    }

    /**
     * 取结果集中,查询出来的原始数据
     *
     * @param rs 结果集
     * @param i 指定行数据的列索引
     * @return object
     * @throws SQLException SQLException
     * @throws IOException IOException
     */
    public static Object getCellValue(ResultSet rs, int i) throws SQLException, IOException {
        ResultSetMetaData metaData = rs.getMetaData();
        int type = metaData.getColumnType(i);
        switch (type){
            case Types.VARCHAR:
            case Types.CHAR:
            case Types.NCHAR:
            case Types.LONGNVARCHAR:
            case Types.LONGVARBINARY:
            case Types.NVARCHAR:
                return rs.getString(i);
            case Types.CLOB:
            case Types.NCLOB:
                return IOUtils.toString(rs.getClob(i).getCharacterStream());
            case Types.BOOLEAN:
                return rs.getBoolean(i);
            case Types.DATE:
                return SQLKit.javaDate(rs.getDate(i));
            case Types.TIME:
                return SQLKit.javaDate(rs.getTime(i));
            case Types.TIMESTAMP:
                return SQLKit.javaDate(rs.getTimestamp(i));
            case Types.INTEGER:
            case Types.TINYINT:
            case Types.SMALLINT:
                return rs.getInt(i);
            case Types.BIGINT:
                return rs.getLong(i);
            case Types.DOUBLE:
            case Types.DECIMAL:
            case Types.NUMERIC:
            case Types.FLOAT:
                return rs.getDouble(i);
            case Types.NULL:
                return null;
            default:
                rs.getString(i);
        }
        return null;
    }

    private static List<Map<String,Object>> pickUpCtxParam(List<Map<String,Object>> mapList){
        List<Map<String,Object>> retList = ListKit.newArrayList();
        for(Object object : mapList){
            if(object instanceof Map){
                Map<String,Object> mapItem = MapKit.newHashMap();
                mapItem.putAll((Map<String,Object>)object);
                Map<String,Object> pickupItem = pickUpCtxParam(mapItem);
                retList.add(pickupItem);
            }
        }
        return retList;
    }
    private static Map<String,Object> pickUpCtxParam(Map<String,Object> map){
        Map<String,Object> ctxParam = MapKit.newLinkedHashMap();
        if(LOG_SQL_CTX_PARAM_LIST!=null&&LOG_SQL_CTX_PARAM_LIST.size()>0){
            for(String name : LOG_SQL_CTX_PARAM_LIST){
                if(map.containsKey(name)){
                    Object value = map.get(name);
                    ctxParam.put(name,value);
                    map.remove(name);
                }
            }
        }
        return ctxParam;
    }

    public static void logSQL(Logger logger,String summary, String sql, Object param, int paramSize, int result, long costTime){

        //对于上下文保留变量，要作提取处理
        Object paramShow = param;
        Map<String,Object> mapParam = MapKit.newHashMap();
        Map<String,Object> pickMapParam = MapKit.newHashMap();
        List<Map<String,Object>> listMapParam = null;
        List<Map<String,Object>> pickListMapParam = null;
        if(param instanceof Map){
            mapParam.putAll((Map<String,Object>)param); //复制
            pickMapParam = pickUpCtxParam(mapParam);
            paramShow = mapParam;
        }else if(param instanceof List){
            listMapParam = (List<Map<String,Object>>)param;
            pickListMapParam = pickUpCtxParam(listMapParam);
            paramShow = listMapParam;
        }

        logger.debug(StringKit.format("┏━━━━━━━━━━ SQL LOG [{0}] ━━━━━━━━━━",summary));
        logger.debug(StringKit.format("┣ SQL:           {0}",sql));

        if(paramSize<=1){
            logger.debug(StringKit.format("┣ 参数:            {0}", JSONKit.toJsonString(paramShow)));
            if(pickMapParam != null && pickMapParam.size()>0){
                logger.debug(StringKit.format("┣ 上下文参数:         {0}", JSONKit.toJsonString(pickMapParam)));
            }
        }else{
            logger.debug(StringKit.format("┣ 参数([{1}]条):    {0}", JSONKit.toJsonString(paramShow),paramSize));
            if(pickListMapParam != null && pickListMapParam.size()>0){
                logger.debug(StringKit.format("┣ 上下文参数([{1}]条): {0}", JSONKit.toJsonString(pickListMapParam)));
            }
        }

        logger.debug(StringKit.format("┣ 影响记录/耗时:   [{0}]/[{1}ms]",result,costTime/100));
        logger.debug(StringKit.format("┗━━━━━━━━━━ SQL LOG [{0}] ━━━━━━━━━━",summary));
    }

}
