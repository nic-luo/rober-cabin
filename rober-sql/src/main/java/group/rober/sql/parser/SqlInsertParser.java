package group.rober.sql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.dialect.SqlDialectType;

import java.util.ArrayList;
import java.util.List;

/**
 * INSERT语句解析，只解析简单的插入语句，复杂的暂时不支持
 */
public class SqlInsertParser extends SqlParser {
    private String table;
    private List<PairBond<String,String>> fields = new ArrayList<PairBond<String,String>>();

    public SqlInsertParser(){
    }

    public SqlInsertParser(String sql, SqlDialectType sqlDialectType) {
        this.sql = sql;
        this.sqlDialectType = sqlDialectType;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }


    public String getTable() {
        return table;
    }

    public List<PairBond<String, String>> getFields() {
        return fields;
    }

    public SqlInsertParser parse(){
        String dbType = getDbType(sqlDialectType);

        SQLStatementParser stmtParser = new SQLStatementParser(sql,dbType);
        SQLStatement stmt = stmtParser.parseStatement();
        SQLInsertStatement insertStmt = (SQLInsertStatement)stmt;

        List<SQLExpr> columns = insertStmt.getColumns();
//        List<SQLInsertStatement.ValuesClause> values = insertStmt.getValuesList();
        this.table = insertStmt.getTableName().getSimpleName();

        SQLInsertStatement.ValuesClause valuesClause = insertStmt.getValues();
        List<SQLExpr> valuesExpr = valuesClause.getValues();

        for(int i=0;i<columns.size();i++){
            PairBond<String,String> item = new PairBond<String,String>();
            item.setLeft(columns.get(i).toString());
            item.setRight(valuesExpr.get(i).toString());
            fields.add(item);
        }

        return this;
    }
}
