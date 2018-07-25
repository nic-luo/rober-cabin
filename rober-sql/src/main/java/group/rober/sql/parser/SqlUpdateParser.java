package group.rober.sql.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.dialect.SqlDialectType;

import java.util.ArrayList;
import java.util.List;

/**
 * UPDATE语句解析，暂时不支持多层嵌套的解析
 */
public class SqlUpdateParser extends SqlParser{

    private String table;
    private List<PairBond<String,String[]>> fields = new ArrayList<PairBond<String,String[]>>();

    public SqlUpdateParser(){
    }



    public SqlUpdateParser(String sql, SqlDialectType sqlDialectType) {
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

    public List<PairBond<String, String[]>> getFields() {
        return fields;
    }

    public SqlUpdateParser parse(){
        String dbType = getDbType(sqlDialectType);

        SQLStatementParser stmtParser = new SQLStatementParser(sql,dbType);
        SQLStatement stmt = stmtParser.parseStatement();
        SQLUpdateStatement updateStmt = (SQLUpdateStatement)stmt;
//        SQLTableSource tableSource = updateStmt.getTableSource();

        this.table = updateStmt.getTableName().getSimpleName();

        List<SQLUpdateSetItem> items = updateStmt.getItems();
        for(SQLUpdateSetItem item : items){
            SQLExpr column = item.getColumn();
            SQLExpr value = item.getValue();
            fields.add(new PairBond<String,String[]>(column.toString(),new String[]{value.toString()}));
        }

        SQLExpr whereExpr = updateStmt.getWhere();
        System.out.println(whereExpr.getClass().getName());
        if(whereExpr instanceof SQLBinaryOpExpr){
            SQLBinaryOpExpr expr = (SQLBinaryOpExpr)whereExpr;
            parseWhere(expr);
        }

        return this;
    }

    private void parseWhere(SQLBinaryOpExpr expr){
        SQLExpr left = expr.getLeft();
        SQLExpr right = expr.getRight();

        if(left instanceof SQLIdentifierExpr && right instanceof SQLVariantRefExpr){
            SQLIdentifierExpr nameExpr = (SQLIdentifierExpr)left;
            SQLVariantRefExpr valueExpr = (SQLVariantRefExpr)right;

            PairBond<String,String[]> pairBond = new PairBond<String,String[]>();
            pairBond.setLeft(nameExpr.getName());
            pairBond.setRight(new String[]{valueExpr.getName()});
            fields.add(pairBond);
        }

        if(left instanceof SQLBinaryOpExpr){
            parseWhere((SQLBinaryOpExpr)left);
        }
        if(right instanceof SQLBinaryOpExpr){
            parseWhere((SQLBinaryOpExpr)right);
        }
        if(left instanceof SQLBetweenExpr){
            SQLBetweenExpr betExpr = (SQLBetweenExpr)left;
            fillSQLBetweenExpr(betExpr);
        }
        if(right instanceof SQLBetweenExpr){
            SQLBetweenExpr betExpr = (SQLBetweenExpr)right;
            fillSQLBetweenExpr(betExpr);
        }
    }

    private void fillSQLBetweenExpr(SQLBetweenExpr betExpr){
        PairBond<String,String[]> pairBond = new PairBond<String,String[]>();
        pairBond.setLeft(betExpr.getTestExpr().toString());
        pairBond.setRight(new String[]{betExpr.getBeginExpr().toString(),betExpr.getEndExpr().toString()});
        fields.add(pairBond);
    }



}
