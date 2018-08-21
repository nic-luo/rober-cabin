package group.rober.sql.core;

import group.rober.runtime.kit.SQLKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.kit.ValidateKit;
import group.rober.runtime.lang.PairBond;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL选择语句实体类
 */
public class SqlQuery implements Serializable,Cloneable{
    private String select;
    private List<PairBond> selectItems = new ArrayList<PairBond>();
    private String from;
    private String where;
    private String groupBy;
    private String having;
    private String orderBy;

    public SqlQuery() {
    }

    public SqlQuery(String from) {
        this.from = from;
    }

    public SqlQuery(String select, String from, String where) {
        this.select = select;
        this.from = from;
        this.where = where;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public List<PairBond> getSelectItems() {
        return selectItems;
    }

    public void setSelectItems(List<PairBond> selectItems) {
        this.selectItems = selectItems;
    }

    public String getFrom() {
        return from;
    }

    public SqlQuery setFrom(String from) {
        this.from = from;
        return this;
    }

    public String getWhere() {
        return where;
    }

    public SqlQuery setWhere(String where) {
        this.where = where;
        return this;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(String groupBy) {
        this.groupBy = groupBy;
    }

    public String getHaving() {
        return having;
    }

    public SqlQuery setHaving(String having) {
        this.having = having;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public SqlQuery setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public String buildQuerySql(){
        return buildQuerySql(false);
    }

    public String buildQuerySql(boolean columnAlias){
        StringBuffer bufferSql = new StringBuffer();
        String _select = StringKit.nvl(select,"SELECT");
        ValidateKit.notBlank(from,"SQL语句错误，FROM部分无内容");

        bufferSql.append(_select).append(" ");

        if(selectItems!=null&&selectItems.size()>0){
            for(int i=0;i<selectItems.size();i++){
                PairBond<String,String> item = selectItems.get(i);
                String alias = item.getLeft();
                String column = item.getRight();
                if(StringKit.isBlank(column))continue;
                bufferSql.append(column);
                //不是虚字段的情况下，才使用别名
                if(!SQLKit.isConstColumn(column) && columnAlias && StringKit.isNotBlank(alias)){
                    bufferSql.append(" AS ").append(alias);
                }
                if(i<selectItems.size()-1){
                    bufferSql.append(",");
                }
            }
        }else{
            bufferSql.append("*");
        }

        bufferSql.append(" FROM ").append(from);

        if(StringKit.isNotBlank(where)) bufferSql.append(" WHERE ").append(where);
        if(StringKit.isNotBlank(groupBy)) bufferSql.append(" GROUP BY ").append(groupBy);
        if(StringKit.isNotBlank(having)) bufferSql.append(" HAVING ").append(having);
        if(StringKit.isNotBlank(orderBy)) bufferSql.append(" ORDER BY ").append(orderBy);

        return bufferSql.toString();
    }
}
