package group.rober.sql.sqlfile;

/**
 * SQL文件加载器
 */
public interface SQLTextLoader {
    SQLCollecter parse(String... resource);
}
