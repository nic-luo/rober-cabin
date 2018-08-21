package group.rober.sql.jdbc.runner;

/**
 * 定义文本解析异常类
 *
 * @author syang
 * @since 2014/02/25
 *
 */
public class TextParseException extends Exception {

	private static final long serialVersionUID = 82734950866419781L;

	public TextParseException() {
		super();
	}

	public TextParseException(String message, Throwable cause) {
		super(message, cause);
	}

	public TextParseException(String message) {
		super(message);
	}

	public TextParseException(Throwable cause) {
		super(cause);
	}
}
