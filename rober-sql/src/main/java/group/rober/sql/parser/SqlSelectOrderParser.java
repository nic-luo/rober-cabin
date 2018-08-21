package group.rober.sql.parser;

import group.rober.sql.dialect.SqlDialectType;

public class SqlSelectOrderParser extends SqlParser {

    public SqlSelectOrderParser() {
    }

    public SqlSelectOrderParser(String sql, SqlDialectType sqlDialectType) {
        this.sql = sql;
        this.sqlDialectType = sqlDialectType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public SqlSelectOrderParser parse(){

        String dbType = getDbType(sqlDialectType);

        return this;
    }
}
