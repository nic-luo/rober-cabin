package group.rober.sql.jdbc.runner;

/**
 * 单行文本处理器
 *
 * @author syang
 * @since 2014/02/25
 */
public interface TextLineProcess {
	/**
	 * 执行处理
	 *
	 * @param lineContent lineContent
	 * @param skipError skipError
	 * @throws TextParseException TextParseException
	 */
	public void process(String lineContent, boolean skipError) throws TextParseException;
}