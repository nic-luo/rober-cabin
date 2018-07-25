package group.rober.sql.dialect;

/**
 * 处理不同的数据库在分页以及数据库差异化的分另处理上
 */
public interface SqlDialect {
    /**
     * 处理成不同的分页查询SQL
     *
     * @param sql
     * @param index
     * @param size
     * @return
     */
    public String getPaginationSql(String sql,int index,int size);

    /**
     * 处理查询总数量SQL
     *
     * @param sql
     * @return
     */
    public String getCountSql(String sql);
}
