package group.rober.office.excel.imports.config;


import group.rober.office.excel.imports.exception.ExcelImportException;

/**
 * 配置异常
 *
 * @author yangsong
 * @since 2014/04/02
 *
 */
public class ConfigException extends ExcelImportException {
	private static final long serialVersionUID = -5233157343520417926L;

	public ConfigException() {
		super();
	}

	public ConfigException(String message, Throwable e) {
		super(message, e);
	}

	public ConfigException(String message) {
		super(message);
	}

	public ConfigException(Throwable e) {
		super(e);
	}

}
