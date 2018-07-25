package group.rober.sql.core;

import group.rober.runtime.kit.JSONKit;
import group.rober.runtime.kit.SQLKit;
import group.rober.runtime.kit.StringKit;
import group.rober.sql.converter.NameConverter;
import group.rober.sql.converter.impl.UnderlinedNameConverter;
import group.rober.sql.dialect.SqlDialectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class AbstractUpdater {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    protected NamedParameterJdbcTemplate jdbcTemplate;
    protected SqlDialectType sqlDialectType = SqlDialectType.MYSQL;
    private ThreadLocal<NameConverter> nameConverterThreadLocal = new ThreadLocal<NameConverter>();

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public SqlDialectType getSqlDialectType() {
        return sqlDialectType;
    }

    public void setSqlDialectType(SqlDialectType sqlDialectType) {
        this.sqlDialectType = sqlDialectType;
    }

    public int execute(String sql){
        return execute(sql,new HashMap<String, Object>());
    }
    public int execute(String sql,Map<String,?> paramMap){
        long startTime = System.currentTimeMillis();
        int ret = jdbcTemplate.update(sql,paramMap);
        long endTime = System.currentTimeMillis();
        logSQL("Execute",sql,paramMap,1,ret,endTime-startTime);
        return ret;
    }

    protected void logSQL(String summary,String sql,Object param,int paramSize,int result,long costTime){
        SQLKit.logSQL(logger,summary,sql,param,paramSize,result,costTime);
    }
//
//    protected NameConverter getNameConverter(){
//        NameConverter nameConverter = nameConverterThreadLocal.get();
//        if(nameConverter==null){
//            nameConverter = new UnderlinedNameConverter();
//        }else{
//            if(remove){
//                nameConverterThreadLocal.remove();
//            }
//        }
//        return nameConverter;
//    }

    protected NameConverter getNameConverter(){
        return nameConverterThreadLocal.get();
    }


    protected AbstractUpdater setNameConverter(NameConverter nameConverter){
        nameConverterThreadLocal.set(nameConverter);
        return this;
    }

    protected AbstractUpdater removeNameConverter(){
        NameConverter nameConverter = nameConverterThreadLocal.get();
        if(nameConverter!=null){
            nameConverterThreadLocal.remove();
        }
        return this;
    }

    public <T> T exec(NameConverter nameConverter,MapDataExecutor<T> executor){
        T r = null;
        try{
            setNameConverter(nameConverter);
            r = executor.impl();
        }catch(Exception e){
            throw e;
        }finally {
            removeNameConverter();
        }

        return r;
    }
}
