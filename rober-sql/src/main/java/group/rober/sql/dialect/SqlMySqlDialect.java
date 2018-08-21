package group.rober.sql.dialect;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.sql.parser.SqlSelectParser;

public class SqlMySqlDialect extends SqlDialectAbstract {

    public String getCountSql(String sql) {
        SqlSelectParser parser = new SqlSelectParser(sql, SqlDialectType.MYSQL);
        parser.setSql(sql);
        parser.parse();

        String whereClause = parser.getWhere();

        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT COUNT(1)");
        buffer.append(" FROM ").append(parser.getFrom());
        if(StringKit.isNotBlank(whereClause)){
            buffer.append(" WHERE ");
            buffer.append(parser.getWhere());
        }

        return buffer.toString();
    }

    public String getPaginationSql(String sql,int index,int size) {
        if(size <= 0) return sql;

        //select * from DEMO_PERSON where CODE > :code LIMIT ${起始行索引号},${每页记录数}
        StringBuffer sbSql = new StringBuffer(sql);
        //起始行号，从1开始，但是不包含当前行
        ValidateKit.isTrue(index>-1,"pagination index must be greater than -1,index={0}",index);
        sbSql.append(" LIMIT ");
        if(index == 0){ //首页不需要起始行索引号
            sbSql.append(size);
        }else{
            int rowIndex = index*size;
            sbSql.append(rowIndex).append(",").append(size);
        }

        return sbSql.toString();
    }
}
