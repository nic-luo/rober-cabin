package group.rober.runtime.lang;

public class NotExistException extends RoberException{
    public NotExistException() {
    }

    public NotExistException(String message) {
        super(message);
    }

    public NotExistException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public NotExistException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public NotExistException(Throwable cause, String message) {
        super(cause, message);
    }

    public NotExistException(Throwable cause) {
        super(cause);
    }

    public NotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
