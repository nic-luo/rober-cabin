package group.rober.sql.core;

import group.rober.runtime.kit.MapKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.MapData;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.dialect.SqlDialectType;
import group.rober.sql.parser.SqlInsertParser;
import group.rober.sql.parser.SqlUpdateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.jdbc.support.rowset.SqlRowSetMetaData;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * 对MapData的查询以及修改处理，由于无数据类型关联，例如日期类型，在会传入时，可能会被修改为long类型，
 * 因此，在插入之前，要进行一些处理
 */
public class MapDataTypeAutoFit {
    public interface FixDataItem{
        void exec(String valueName);
    }

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private JdbcOperations jdbcOperations;
    private SqlDialectType sqlDialectType;

    public MapDataTypeAutoFit(JdbcOperations jdbcOperations, SqlDialectType sqlDialectType) {
        this.jdbcOperations = jdbcOperations;
        this.sqlDialectType = sqlDialectType;
    }

    public SqlRowSetMetaData getMetaData(String table){
        String query = StringKit.format("select * from {0} where 1=2");
        SqlRowSet rowSet = jdbcOperations.queryForRowSet(query, MapKit.newEmptyMap());
        SqlRowSetMetaData meta = rowSet.getMetaData();
        return meta;
    }

    public List<String> getDateColumns(SqlRowSetMetaData meta){
        List<String> fitColumns = new ArrayList<String>();
        for(int i=1;i<=meta.getColumnCount();i++){
            String column = meta.getColumnName(i);
//            String className = meta.getColumnClassName(i);
            int type = meta.getColumnType(i);

            if(type == Types.DATE || type == Types.TIMESTAMP){
                fitColumns.add(column);
            }
        }
        return fitColumns;
    }

    public void fitForUpdate(String sql,FixDataItem fixDataItem){
        SqlUpdateParser parser = null;
        try{
            parser = new SqlUpdateParser(sql,sqlDialectType);
        }catch(Exception e){
            logger.warn("解析SQL出错。SQL："+sql,e);
            return ;
        }
        SqlRowSetMetaData meta = getMetaData(parser.getTable());

        List<String> fitColumns = getDateColumns(meta);
        if(fitColumns.size()==0)return;


        //把日期类型的字段，做个类型转换修正
        List<PairBond<String, String[]>> fields = parser.getFields();
        for(String column : fitColumns){
            for(PairBond<String, String[]> field : fields){
                if(field.getLeft().equalsIgnoreCase(column)){
                    String[] values = field.getRight();
                    if(values==null)continue;
                    for(String v : values){
                        fixDataItem.exec(v);
                    }
                }
            }
        }
    }

    /**
     * 根据数据表，根据数据表的源数据数据类型，更正相应字段的数据类型，典型的如日期被转为了long类型时，需要自动转回
     *
     * @param sql sql
     * @param entity entity
     */
    public void fitForUpdate(String sql,final MapData entity){
        fitForUpdate(sql, new FixDataItem() {
            public void exec(String valueName) {
                fitDataItem(valueName,entity);
            }
        });
    }

    /**
     * 根据数据表，根据数据表的源数据数据类型，更正相应字段的数据类型，典型的如日期被转为了long类型时，需要自动转回
     *
     * @param sql sql
     * @param entityList entityList
     */
    public void fitForUpdate(String sql,final List<MapData> entityList){
        fitForUpdate(sql, new FixDataItem() {
            public void exec(String valueName) {
                for(MapData entity : entityList){
                    fitDataItem(valueName,entity);
                }
            }
        });
    }

    private void fitDataItem(String valueName,MapData entity){
        if(StringKit.isBlank(valueName))return;
        String vn = valueName.replaceAll("^:","");
        if(StringKit.isBlank(vn))return;
        entity.put(vn,entity.getValue(vn).dateValue());
    }

    public void fitForInsert(String sql,FixDataItem fixDataItem){
        SqlInsertParser parser = null;
        try{
            parser = new SqlInsertParser(sql,sqlDialectType);
        }catch(Exception e){
            logger.warn("解析SQL出错。SQL："+sql,e);
            return ;
        }
        SqlRowSetMetaData meta = getMetaData(parser.getTable());
        List<String> fitColumns = getDateColumns(meta);

        if(fitColumns.size()==0)return;

        //把日期类型的字段，做个类型转换修正
        List<PairBond<String, String>> fields = parser.getFields();
        for(String column : fitColumns){
            for(PairBond<String, String> field : fields){
                if(field.getLeft().equalsIgnoreCase(column)){
                    String value = field.getRight();
                    fixDataItem.exec(value);
                }
            }
        }
    }

    /**
     * 根据数据表，根据数据表的源数据数据类型，更正相应字段的数据类型，典型的如日期被转为了long类型时，需要自动转回
     *
     * @param sql sql
     * @param entity entity
     */
    public void fitForInsert(String sql,final MapData entity){
        fitForInsert(sql, new FixDataItem() {
            public void exec(String valueName) {
                fitDataItem(valueName,entity);
            }
        });
    }

    /**
     * 根据数据表，根据数据表的源数据数据类型，更正相应字段的数据类型，典型的如日期被转为了long类型时，需要自动转回
     *
     * @param sql sql
     * @param entityList entityList
     */
    public void fitForInsert(String sql,final List<MapData> entityList){
        fitForInsert(sql, new FixDataItem() {
            public void exec(String valueName) {
                for(MapData entity : entityList){
                    fitDataItem(valueName,entity);
                }
            }
        });
    }


}
