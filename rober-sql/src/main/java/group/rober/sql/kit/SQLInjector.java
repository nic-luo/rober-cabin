package group.rober.sql.kit;

import group.rober.runtime.kit.ListKit;
import group.rober.runtime.kit.StringKit;
import group.rober.runtime.lang.PairBond;
import group.rober.sql.dialect.SqlDialectType;
import group.rober.sql.parser.SqlSelectParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SQL注入器，用于对SQL进行一些注入调整
 */
public abstract class SQLInjector {

    /**
     * 注入排序数据
     *
     * @param dialectType dialectType
     * @param sql       SQL语句
     * @param sortRules 排序规则KEY=数据库字段名，VALUE=ASC/DESC
     * @return String
     */
    public static String injectSQLOrder(SqlDialectType dialectType, String sql, Map<String, String> sortRules) {
//        SqlDialectType dialectType = mapDataAccessor.getMapDataQuery().getSqlDialectType();
        SqlSelectParser parser = new SqlSelectParser(sql, dialectType);
        parser.parse();

        String select = parser.getSelect();
        String from = parser.getFrom();
        String where = parser.getWhere();
        List<PairBond<String, String>> sqlOrderItems = parser.getOrderItems();
        List<PairBond<String, String>> newOrderItems = new ArrayList<>();

        StringBuffer sqlBuffer = new StringBuffer();
        sqlBuffer.append(" SELECT ").append(select);
        sqlBuffer.append(" FROM ").append(from);
        if (StringKit.isNotBlank(where)) {
            sqlBuffer.append(" WHERE ").append(where);
        }

        //规则配置的排序，放到最前面
        Iterator<String> iterator = sortRules.keySet().iterator();
        while (iterator.hasNext()) {
            String column = iterator.next();
            String r = sortRules.get(column);
            if (r.equalsIgnoreCase("ASC")) {
                r = "ASC";
            } else {
                r = "DESC";
            }
            newOrderItems.add(new PairBond<String, String>(column, r));
        }

        //把SQL中本来的排序拼上去，如果SQL中有的和传入的排序规则重复，使用传入的排序规则
        for (PairBond<String, String> pair : sqlOrderItems) {
            String column = pair.getLeft();
            if (!sortRules.containsKey(column)) {
                if("ASC".equalsIgnoreCase(pair.getRight())||"DESC".equalsIgnoreCase(pair.getRight())){
                    newOrderItems.add(pair);

                }
            }
        }


        for (int i = 0; i < newOrderItems.size(); i++) {
            PairBond<String, String> pair = newOrderItems.get(i);
            if (i == 0) {
                sqlBuffer.append(" ORDER BY ");
                sqlBuffer.append(pair.getLeft()).append(" ").append(pair.getRight());
            } else {
                sqlBuffer.append(",").append(pair.getLeft()).append(" ").append(pair.getRight());
            }
        }

        return sqlBuffer.toString();
    }
}
