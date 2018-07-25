package group.rober.sql.autoconfigure;

import group.rober.sql.dialect.SqlDialectType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "group.rober.sql", ignoreUnknownFields = true)
public class SqlProperties {
    private SqlDialectType sqlDialectType = SqlDialectType.MYSQL;

    private SerialNoProperties serialNo = new SerialNoProperties();

    /**
     * 每个不同的业务场景，使用什么样的类来生成流水号
     */

    public SqlDialectType getSqlDialectType() {
        return sqlDialectType;
    }

    public void setSqlDialectType(SqlDialectType sqlDialectType) {
        this.sqlDialectType = sqlDialectType;
    }

    public SerialNoProperties getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(SerialNoProperties serialNo) {
        this.serialNo = serialNo;
    }
}
