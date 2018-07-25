package group.rober.sql.exception;

import group.rober.runtime.lang.RoberException;

public class JdbcException extends RoberException {
    public JdbcException() {
    }

    public JdbcException(String message) {
        super(message);
    }

    public JdbcException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public JdbcException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public JdbcException(Throwable cause, String message) {
        super(cause, message);
    }

    public JdbcException(Throwable cause) {
        super(cause);
    }

    public JdbcException(String message, Throwable cause) {
        super(message, cause);
    }

    public JdbcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
