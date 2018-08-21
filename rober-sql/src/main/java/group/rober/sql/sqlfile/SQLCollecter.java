package group.rober.sql.sqlfile;

import java.util.HashMap;
import java.util.Map;

/**
 * SQL收集器，把SQL聚集到一起
 */
public class SQLCollecter {
    private Map<String,String> sqlTextMap ;
    public SQLCollecter(Map<String,String> sqlTextMap){
        this.sqlTextMap = sqlTextMap;
    }
    public String sql(String key){
        return sqlTextMap.get(key);
    }
}
