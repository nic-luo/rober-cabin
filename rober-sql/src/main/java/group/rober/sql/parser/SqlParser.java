package group.rober.sql.parser;


import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2SchemaStatVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleSchemaStatVisitor;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerSchemaStatVisitor;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SchemaStatVisitor;
import com.alibaba.druid.util.JdbcUtils;
import group.rober.sql.dialect.SqlDialectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SQL语法解析器
 * 主要依托druid进行语法解析，同时，为了屏蔽应用层对druid的直接依赖，统一放到这个类中来处理，以降低应用对某个特定技术的直接依赖
 */
public class SqlParser {
    static Map<SqlDialectType,String> dbTypeMap = new HashMap<SqlDialectType,String>();
    static Map<SqlDialectType,SchemaStatVisitor> visitorMap = new HashMap<SqlDialectType,SchemaStatVisitor>();
    static{
        initMap(SqlDialectType.ORACLE,"oracle",new OracleSchemaStatVisitor());
        initMap(SqlDialectType.MYSQL,"mysql",new MySqlSchemaStatVisitor());
        initMap(SqlDialectType.MARIADB,"mariadb",new MySqlSchemaStatVisitor());
        initMap(SqlDialectType.DB2,"db2",new DB2SchemaStatVisitor());
        initMap(SqlDialectType.SQLSERVER,"sqlserver",new SQLServerSchemaStatVisitor());

        initMap(SqlDialectType.SQLITE,"sqlite",new SchemaStatVisitor());
        initMap(SqlDialectType.H2,"h2",new SchemaStatVisitor());
        initMap(SqlDialectType.DERBY,"derby",new SchemaStatVisitor());
        initMap(SqlDialectType.POSTGRESQL,"postgresql",new SchemaStatVisitor());
        initMap(SqlDialectType.HSQL,"hsql",new SchemaStatVisitor());
        initMap(SqlDialectType.HBASE,"hbase",new SchemaStatVisitor());

    }

    private static void initMap(SqlDialectType sqlDialectType,String db,SchemaStatVisitor visitor){
        dbTypeMap.put(sqlDialectType,db);
        visitorMap.put(sqlDialectType,visitor);
    }

    protected String sql;
    protected SqlDialectType sqlDialectType;
    protected boolean prettyFormat = false;
    protected boolean upperCase = false;

    public boolean isPrettyFormat() {
        return prettyFormat;
    }

    public void setPrettyFormat(boolean prettyFormat) {
        this.prettyFormat = prettyFormat;
    }

    public boolean isUpperCase() {
        return upperCase;
    }

    public void setUpperCase(boolean upperCase) {
        this.upperCase = upperCase;
    }

    protected String getDbType(SqlDialectType dialectType){
        String dbType = dbTypeMap.get(dialectType);
        if(dbType==null){
            throw new UnsupportedOperationException(dialectType.name()+" database type is not supported");
        }
        return dbType;
    }
}
