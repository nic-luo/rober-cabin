package group.rober.dataform.exception;


import group.rober.runtime.lang.RoberException;

/**
 * Created by tisir yangsong158@qq.com on 2017-05-22
 */
public class DataFormException extends RoberException {

    public DataFormException() {
        super();
    }

    public DataFormException(String message) {
        super(message);
    }

    public DataFormException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public DataFormException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public DataFormException(Throwable cause, String message) {
        super(cause, message);
    }

    public DataFormException(Throwable cause) {
        super(cause);
    }

    public DataFormException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataFormException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
