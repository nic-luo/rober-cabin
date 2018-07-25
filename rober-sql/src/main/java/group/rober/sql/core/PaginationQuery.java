package group.rober.sql.core;

import group.rober.runtime.kit.MapKit;
import group.rober.runtime.lang.PairBond;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 分页查询对象
 * Created by tisir yangsong158@qq.com on 2017-05-23
 */
public class PaginationQuery {
    /** 每页大小,-1表示不分页 */
    protected int size = -1;
    /** 当前页索引(从0开始) */
    protected int index = 0;
    /** 查询语句 */
    protected String query;
    /** 查询语句对应的参数 */
    protected Map<String,Object> parameterMap = MapKit.newEmptyMap();
    /** 是否做当前页的概括统计(小计) */
    protected boolean summary = true;
    /** 是否所有数据的概括统计(合计) */
    protected boolean wholeSummary = true;
    /** 字段的概括统计表达式,Key为字段名,Value为统计表达式,如sum(${COLUMN}) */
    protected Map<PairBond<String,String>,String> summarizesExpressions = new LinkedHashMap<PairBond<String,String>,String>();

    public PaginationQuery() {
    }

    public PaginationQuery(String query) {
        this.query = query;
    }

    public PaginationQuery(String query, Map<String, Object> parameterMap) {
        this.query = query;
        this.parameterMap = parameterMap;
    }

    public PaginationQuery(String query, Map<String, Object> parameterMap,int index,int size ) {
        this.query = query;
        this.parameterMap = parameterMap;
        this.index = index;
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public PaginationQuery setSize(int size) {
        this.size = size;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public PaginationQuery setIndex(int index) {
        this.index = index;
        return this;
    }

    public String getQuery() {
        return query;
    }

    public PaginationQuery setQuery(String query) {
        this.query = query;
        return this;
    }

    public boolean isSummary() {
        return summary;
    }

    public PaginationQuery setSummary(boolean summary) {
        this.summary = summary;
        return this;
    }

    public boolean isWholeSummary() {
        return wholeSummary;
    }

    public PaginationQuery setWholeSummary(boolean wholeSummary) {
        this.wholeSummary = wholeSummary;
        return this;
    }

    public Map<PairBond<String,String>, String> getSummarizesExpressions() {
        return summarizesExpressions;
    }

    public PaginationQuery setSummarizesExpressions(Map<PairBond<String,String>, String> summarizesExpressions) {
        this.summarizesExpressions = summarizesExpressions;
        return this;
    }

    public PaginationQuery addSummaryExpression(PairBond<String,String> name, String expression){
        this.summarizesExpressions.put(name,expression);
        return this;
    }
    public PaginationQuery addParameter(String name, Object value){
        parameterMap.put(name,value);
        return this;
    }
    public Object removeParameter(String name,Object value){
        return parameterMap.remove(name);
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }
}
