package group.rober.sql.core;

import group.rober.sql.dialect.SqlDialect;
import group.rober.sql.dialect.SqlDialectType;
import group.rober.sql.dialect.SqlMySqlDialect;
import group.rober.sql.dialect.SqlOracleDialect;

import java.util.HashMap;
import java.util.Map;

public abstract class SqlConsts {
    public static Map<SqlDialectType,SqlDialect> DIALECT_MAP = new HashMap<SqlDialectType,SqlDialect>();
    static {
        DIALECT_MAP.put(SqlDialectType.MYSQL,new SqlMySqlDialect());
        DIALECT_MAP.put(SqlDialectType.ORACLE,new SqlOracleDialect());
    }
}
