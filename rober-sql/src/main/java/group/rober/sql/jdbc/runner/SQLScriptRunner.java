package group.rober.sql.jdbc.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

/**
 * 包装一条可执行SQL语句
 *
 * @author yangsong
 * @since  2013年6月，宁波银行项目组
 * @since  2014/02/13
 * @since  2014/02/24 分离文本解析部分，以便校验功能能够复用此模块
 * @since  2018/08/09 重构后，放到rober-sql模块中
 */
public class SQLScriptRunner {
    protected Logger logger = LoggerFactory.getLogger(getClass());


    private String content;
    private TextParse.ParseType parseType = TextParse.ParseType.Content;
    private String delimiter = ";";                //SQL语句分割符
    private boolean skipAllError = false;        //是否忽略跳过所有错误
    private long sqlWarmTime = 30000;            //单条SQL执行警告时间
    private Statement stmt = null;

    public SQLScriptRunner(String content, Statement stmt) {
        this.content = content;
        this.stmt = stmt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TextParse.ParseType getParseType() {
        return parseType;
    }

    public void setParseType(TextParse.ParseType parseType) {
        this.parseType = parseType;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public boolean isSkipAllError() {
        return skipAllError;
    }

    public void setSkipAllError(boolean skipAllError) {
        this.skipAllError = skipAllError;
    }

    public long getSqlWarmTime() {
        return sqlWarmTime;
    }

    public void setSqlWarmTime(long sqlWarmTime) {
        this.sqlWarmTime = sqlWarmTime;
    }

    public Statement getStmt() {
        return stmt;
    }

    public void setStmt(Statement stmt) {
        this.stmt = stmt;
    }

    public void exec() throws TextParseException {
        //构造解析器，并且生成一个解析单行原子文本执行器
        TextParse textParse = new TextParse(delimiter, content, parseType, new TextLineProcess() {
            public void process(String lineContent, boolean skipError)
                    throws TextParseException {
//                lineContent = replaceEnvVar(lineContent);
                SQLExecItem execItem = new SQLExecItem(lineContent);
                try {
                    execItem.exec(stmt, skipError || skipAllError, sqlWarmTime);
                    logger.debug("-----------------------------\n");
                } catch (SQLException e) {
                    throw new TextParseException(MessageFormat.format("执行SQL出错,SQL:{0}", lineContent),e);
                }
            }
        });
        textParse.parse();
    }


}
