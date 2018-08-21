package group.rober.sql.core;

import com.alibaba.druid.sql.SQLUtils;
import group.rober.runtime.kit.MapKit;
import group.rober.runtime.kit.NumberKit;
import group.rober.runtime.kit.SQLKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.dialect.SqlDialect;
import group.rober.sql.dialect.SqlDialectType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 抽象通过查询
 */
public class AbstractQuery {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    private ThreadLocal<RowMapper<?>> rowMapperThreadLocal = new ThreadLocal<RowMapper<?>>();

    protected SqlDialectType sqlDialectType = SqlDialectType.MYSQL;
    protected NamedParameterJdbcTemplate jdbcTemplate;

    public SqlDialectType getSqlDialectType() {
        return sqlDialectType;
    }

    public void setSqlDialectType(SqlDialectType sqlDialectType) {
        this.sqlDialectType = sqlDialectType;
    }

    public NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected void logSQL(String summary, String sql, Object param, int paramSize, int result, long costTime) {
        SQLKit.logSQL(logger, summary, sql, param, paramSize, result, costTime);
    }

    protected String prettySqlFormat(String sql) {
        return SQLUtils.format(sql, sqlDialectType.name().toLowerCase(), new SQLUtils.FormatOption(true, true));
    }

    /**
     * 小计以及合计部分的处理
     *
     * @param paginationData
     * @param query
     * @param paginationSql
     * @param sql
     */
    private void parseSummaryQuery(PaginationData paginationData, PaginationQuery query, String paginationSql, String sql) {
        //如果统计表达式有值，那么说明要做小计，合计小计，合计处理
        Map<PairBond<String,String>, String> summarizesExpressions = query.getSummarizesExpressions();
        if (summarizesExpressions != null && summarizesExpressions.size() > 0) {
            Iterator<PairBond<String,String>> iterator = summarizesExpressions.keySet().iterator();
            StringBuffer summaryClause = new StringBuffer();
            while (iterator.hasNext()) {
                PairBond<String,String> column = iterator.next();
                String expression = summarizesExpressions.get(column);
                if (StringKit.isBlank(expression)) continue;
                expression = expression.replaceAll("\\$\\{\\s*COLUMN\\s*\\}", column.getLeft());

                summaryClause.append(expression).append(" AS ").append(column.getRight()).append(",");
            }
            //删除最末尾的逗号
            if (summaryClause.length() > 0) summaryClause.deleteCharAt(summaryClause.length() - 1);

            //小计
            if (query.isSummary() && StringKit.isNotBlank(paginationSql)) {
                StringBuffer summaySQL = new StringBuffer();
                summaySQL.append("SELECT ")
                        .append(summaryClause)
                        .append(" FROM ")
                        .append("(")
                        .append(paginationSql)
//                        .append(") AS VS_TABLE");
                        .append(") VS_TABLE");

                long t1 = System.currentTimeMillis();
                Map<String, Object> row = jdbcTemplate.queryForMap(summaySQL.toString(), query.getParameterMap());
                long t2 = System.currentTimeMillis();
                logSQL("SQL查询小计", prettySqlFormat(summaySQL.toString()), query.getParameterMap(), -1, 1, t2 - t1);
                paginationData.setSummarizes(row);
            }
            //合计
            if (query.isWholeSummary() && StringKit.isNotBlank(sql)) {
                StringBuffer summaySQL = new StringBuffer();
                summaySQL.append("SELECT ")
                        .append(summaryClause)
                        .append(" FROM ")
                        .append("(")
                        .append(sql)
                        .append(") VS_TABLE");
//                        .append(") AS VS_TABLE");

                long t1 = System.currentTimeMillis();
                Map<String, Object> row = jdbcTemplate.queryForMap(summaySQL.toString(), query.getParameterMap());
                long t2 = System.currentTimeMillis();
                logSQL("SQL查询合计", prettySqlFormat(summaySQL.toString()), query.getParameterMap(), -1, 1, t2 - t1);
                paginationData.setTotalSummarizes(row);
            }
        }
    }

    public <T> PaginationData<T> selectListPagination(PaginationQuery query, RowMapper<T> rowMapper) {
        SqlDialect sqlDialect = getSqlDialect();
        String sql = query.getQuery();
//        logger.debug("[====================SQL分页查询====================][开始]");

        PaginationData pqr = new PaginationData();
        if (query.getSize() > 0) {
            //根据不同的数据库方言，选择合适的策略处理分页
            String paginationSql = sqlDialect.getPaginationSql(sql, query.getIndex(), query.getSize());
//        logger.debug(prettySqlFormat(paginationSql));
//        logger.debug("参数:"+ JSONKit.toJsonStringNowrap(query.getParameterMap()));

            long startTime = System.currentTimeMillis();
            List<T> dataList = null;
            try {
                dataList = jdbcTemplate.query(paginationSql, query.getParameterMap(), rowMapper);
            } catch (DataAccessException e) {
                logger.error("执行SQL出错：" + paginationSql, e);
            }
            long endTime = System.currentTimeMillis();
//        logger.debug("耗时:("+ (endTime-startTime)+")毫秒");
//        logger.debug("[====================SQL分页查询====================][结束]");
            logSQL("SQL分页查询", prettySqlFormat(paginationSql), query.getParameterMap(), -1, dataList.size(), endTime - startTime);
            int totalCount = selectCount(sql, query.getParameterMap());
            int pageCount = NumberKit.ceil(NumberKit.divide(totalCount, query.getSize()), 0).intValue();

            //处理返回结果部
            pqr.setDataList(dataList);
            pqr.setIndex(query.getIndex());
            pqr.setSize(query.getSize());
            pqr.setRowCount(dataList.size());
            pqr.setTotalRowCount(totalCount);
            pqr.setPageCount(pageCount);

            //处理小计以及合计部分
            parseSummaryQuery(pqr, query, paginationSql, sql);
        }else{//如果分页大小小于0，表示不分页
            long startTime = System.currentTimeMillis();
            List<T> dataList = null;
            try {
                dataList = jdbcTemplate.query(sql, query.getParameterMap(), rowMapper);
            } catch (DataAccessException e) {
                logger.error("执行SQL出错：" + sql, e);
            }
            long endTime = System.currentTimeMillis();
            logSQL("SQL不分页查询", prettySqlFormat(sql), query.getParameterMap(), -1, dataList.size(), endTime - startTime);

            //处理返回结果部
            pqr.setDataList(dataList);
            pqr.setIndex(0);
            pqr.setSize(0);
            pqr.setRowCount(dataList.size());
            pqr.setTotalRowCount(dataList.size());
            pqr.setPageCount(1);

            //处理小计以及合计部分,由于不分页，因此小计部分不处理,小计=合计
            parseSummaryQuery(pqr, query, null, sql);
            pqr.setSummarizes(pqr.getTotalSummarizes());

        }



        return pqr;
    }

    public Integer selectCount(String sql, Map<String, ?> paramMap) {
        SqlDialect sqlDialect = getSqlDialect();
        String countSql = sqlDialect.getCountSql(sql);

        long startTime = System.currentTimeMillis();
        int r = jdbcTemplate.queryForObject(countSql, paramMap, new RowMapper<Integer>() {
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt(1);
            }
        });
        long endTime = System.currentTimeMillis();
        logSQL("SQL分页查询统计", countSql, paramMap, -1, 1, endTime - startTime);

        return r;
    }

    public SqlDialect getSqlDialect() {
        SqlDialect sqlDialect = SqlConsts.DIALECT_MAP.get(sqlDialectType);
        if (sqlDialect == null) {
            throw new UnsupportedOperationException(sqlDialectType.name() + " database type is not supported");
        }
        return sqlDialect;
    }

    public Integer selectCount(String sql, String k1, Object v1) {
        return selectCount(sql, MapKit.mapOf(k1, v1));
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2) {
        return selectCount(sql, MapKit.mapOf(k1, v1, k2, v2));
    }

    public Integer selectCount(String sql, String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        return selectCount(sql, MapKit.mapOf(k1, v1, k2, v2, k3, v3));
    }

    public RowMapper getRowMapper() {
        RowMapper rowMapper = rowMapperThreadLocal.get();
        return rowMapper;
    }

    private AbstractQuery setRowMapper(RowMapper rowMapper) {
        this.rowMapperThreadLocal.set(rowMapper);
        return this;
    }

    private AbstractQuery removeRowMapper(){
        rowMapperThreadLocal.remove();
        return this;
    }
    /**
     * 自定义RowMapper，并且执行
     *
     * @param rowMapper rowMapper
     * @param executor executor
     * @param <T> T
     * @return T
     */
    public <T> T exec(RowMapper rowMapper, MapDataExecutor<T> executor){
        T r = null;
        try{
            setRowMapper(rowMapper);
            r = executor.impl();
        }catch(Exception e){
            throw e;
        }finally {
            removeRowMapper();
        }
        return r;
    }
}
