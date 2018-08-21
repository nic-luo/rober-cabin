package group.rober.sql.autoconfigure;

import group.rober.sql.serialno.constants.CursorRecordType;
import group.rober.sql.serialno.constants.GeneratorType;

import java.util.HashMap;
import java.util.Map;

public class SerialNoProperties {

    /**
     * 默认流水号生成器
     */
    private String defaultGenerator = GeneratorType.DEFAULT;

    /**
     * 默认流水号生成器模板
     */
    private String defaultTemplate = "0000";

    /**
     * 每个不同的业务场景，使用什么样的类来生成流水号
     */
    private Map<String, String> generatorMap = new HashMap<String, String>();

    /**
     * 每个不同的业务场景，使用什么样的模板来生成流水号
     */
    private Map<String, String> templateMap = new HashMap<String, String>();

    /**
     * 数据存储适配器ID
     */
    private String cursorRecord = CursorRecordType.DB_TABLE;

    private String table = "FOWK_SERIAL_NUMBER";
    private String idColumn = "ID";
    private String templateColumn = "TEMPLATE";
    private String valueColumn = "CURSOR_VALUE";
    private String timeColumn = "UPDATED_TIME";

    public String getDefaultGenerator() {
        return defaultGenerator;
    }

    public void setDefaultGenerator(String defaultGenerator) {
        this.defaultGenerator = defaultGenerator;
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }

    public Map<String, String> getGeneratorMap() {
        return generatorMap;
    }

    public void setGeneratorMap(Map<String, String> generatorMap) {
        this.generatorMap = generatorMap;
    }

    public Map<String, String> getTemplateMap() {
        return templateMap;
    }

    public void setTemplateMap(Map<String, String> templateMap) {
        this.templateMap = templateMap;
    }

    public String getCursorRecord() {
        return cursorRecord;
    }

    public void setCursorRecord(String cursorRecord) {
        this.cursorRecord = cursorRecord;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getIdColumn() {
        return idColumn;
    }

    public void setIdColumn(String idColumn) {
        this.idColumn = idColumn;
    }

    public String getTemplateColumn() {
        return templateColumn;
    }

    public void setTemplateColumn(String templateColumn) {
        this.templateColumn = templateColumn;
    }

    public String getValueColumn() {
        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {
        this.valueColumn = valueColumn;
    }

    public String getTimeColumn() {
        return timeColumn;
    }

    public void setTimeColumn(String timeColumn) {
        this.timeColumn = timeColumn;
    }
}
