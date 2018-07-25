package group.rober.sql.audit;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 审计记录数据对象
 */
public class AuditRecord implements Serializable{
    private String requestId;
    private String operateType;
    private String operateAt;
    private String operateSummary;
    private String operator;
    private Date operateTime;
    private LinkedHashMap<String,String> fieldNameMap;
    private LinkedHashMap<String,String> dictMap;
    private LinkedHashMap<String,String> modifiedMap;
    private LinkedHashMap<String,String> orginData;
}
