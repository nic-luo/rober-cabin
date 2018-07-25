package group.rober.sql.dialect;

import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.sql.parser.SqlSelectParser;

import java.util.List;
import java.util.Set;

public class SqlOracleDialect extends SqlDialectAbstract {

    public String getCountSql(String sql) {
        SqlSelectParser parser = new SqlSelectParser(sql, SqlDialectType.ORACLE);
        parser.setSql(sql);
        parser.parse();

        StringBuffer buffer = new StringBuffer();
        buffer.append("SELECT COUNT(1)");
        buffer.append(" FROM ").append(parser.getFrom());

        if(StringKit.isNotBlank(parser.getWhere())){
            buffer.append(" WHERE ");
            buffer.append(parser.getWhere());
        }

        return buffer.toString();
    }

    public String getPaginationSql(String sql,int index,int size) {
        if(size <= 0) return sql;

        //select * from DEMO_PERSON where CODE > :code LIMIT ${起始行索引号},${每页记录数}
        //转变为
        //select * from (
        //                  select rownum as ROW_NO,DEMO_PERSON.* from DEMO_PERSON where CODE > :code
        //                  and rownum <= ${结束行索引号}
        //              ) PGV_TABLE where PGV_TABLE.ROW_NO > ${起始行索引号}

        //起始行号，从1开始，但是不包含当前行
        ValidateKit.isTrue(index>-1,"pagination index must be greater than -1,index={0}",index);

        SqlSelectParser parser = new SqlSelectParser(sql, SqlDialectType.ORACLE);
        parser.setSql(sql);
        parser.parse();

        StringBuffer sqlBuffer = new StringBuffer();
        if(index == 0){//第一页SQL简化
            weaveInnerQuery(sqlBuffer,parser,index,size);
        }else{
            sqlBuffer.append("SELECT * FROM (");
            weaveInnerQuery(sqlBuffer,parser,index,size);
            sqlBuffer.append(") PGV_TABLE");
//            sqlBuffer.append(" WHERE PGV_TABLE.ROW_NO > :__startRowNo");
            sqlBuffer.append(" WHERE PGV_TABLE.ROW_NO > ").append(index*size);
        }

        return sqlBuffer.toString();
    }

    private void weaveInnerQuery(StringBuffer buffer,SqlSelectParser parser,int index,int size){
        String order = parser.getOrder();
        //没有排序情况下，处理比较简单
        if(StringKit.isNotBlank(order)){
            weaveSelectFrom(parser,buffer,parser.getColumns(),parser.getFromTableAlias(),index,size);
        }else{
            //分页处理SELECT部分织入
            weaveSelect(buffer,parser.getColumns(),parser.getFromTableAlias());
            buffer.append(" FROM ").append(parser.getFrom());
            //分页处理WHERE条件织入
            weaveWhere(buffer,parser.getWhere(),index,size);
        }
    }

    private StringBuffer joinColumns(List<String> columnList,Set<String> fromAliaSet){
        StringBuffer buffer = new StringBuffer();
        for(String colum : columnList){
            buffer.append(",");
            //如果是 "*" 则需要对每一个查询的子表增加*
            String cleanColumn = colum.replaceAll("\\s*","");
            if("*".equals(cleanColumn)){
                for(String t : fromAliaSet){
                    buffer.append(t).append(".*");
                }
            }else{
                buffer.append(colum);
            }
        }
        return buffer;
    }

    private void weaveSelectFrom(SqlSelectParser parser,StringBuffer buffer,List<String> columnList,Set<String> fromAliaSet,int index,int size){
        String from = parser.getFrom();
        String where = parser.getWhere();
        String order = parser.getOrder();

        StringBuffer columnsBuffer = joinColumns(columnList,fromAliaSet);
        if(columnsBuffer.charAt(0)==','){
            columnsBuffer.deleteCharAt(0);
        }

        buffer.append("SELECT rownum as ROW_NO ,STV.*") ;
        buffer.append(" FROM (");
        buffer.append("SELECT ");
        buffer.append(columnsBuffer);
        buffer.append(" FROM ").append(from);
        if(StringKit.isNotBlank(where)){
            buffer.append(" WHERE ").append(where);
        }
        if(StringKit.isNotBlank(order)){
            buffer.append(" ").append(order);
        }
        buffer.append(" ) STV");

        buffer.append(" WHERE");
        buffer.append(" rownum <= ").append((index+1)*size);
    }

    private void weaveSelect(StringBuffer buffer,List<String> columnList,Set<String> fromAliaSet){
        buffer.append("SELECT rownum as ROW_NO ");
        buffer.append(joinColumns(columnList,fromAliaSet));
    }

    /**
     * 对Where条件进行织入处理
     *
     * @param buffer
     * @param where
     * @param index
     * @param size
     */
    private void weaveWhere(StringBuffer buffer,String where,int index,int size){
        buffer.append(" WHERE");
        if(StringKit.isNotBlank(where)){
            buffer.append(" ").append(where);
            buffer.append(" AND");
        }
//        buffer.append(" rownum <= :__endRowNo");
        buffer.append(" rownum <= ").append((index+1)*size);
    }
}
