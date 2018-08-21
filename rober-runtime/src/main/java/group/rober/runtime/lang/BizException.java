package group.rober.runtime.lang;

/**
 * 业务异常统一父类
 */
public class BizException extends RoberException {

    public BizException() {
        super();
    }

    public BizException(String message) {
        super(message);
    }

    public BizException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public BizException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public BizException(Throwable cause, String message) {
        super(cause, message);
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
