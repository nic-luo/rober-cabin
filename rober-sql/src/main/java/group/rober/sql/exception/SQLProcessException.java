package group.rober.sql.exception;

import group.rober.runtime.lang.RoberException;

public class SQLProcessException extends RoberException {
    public SQLProcessException() {
    }

    public SQLProcessException(String message) {
        super(message);
    }

    public SQLProcessException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public SQLProcessException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public SQLProcessException(Throwable cause, String message) {
        super(cause, message);
    }

    public SQLProcessException(Throwable cause) {
        super(cause);
    }

    public SQLProcessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLProcessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
