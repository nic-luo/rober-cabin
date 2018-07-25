package group.rober.runtime.lang;

import java.text.MessageFormat;

/**
 * 定义格式化之后的RuntimeException
 *
 * @author tisir yangsong158@qq.com
 * @since  2017年2月18日
 */
public class RoberException extends RuntimeException {

    private static final long serialVersionUID = -2049467256019982005L;
    private String code = "0";


    public RoberException() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    /**
     * @param message message
     */
    public RoberException(String message) {
        super(message);
    }


    /**
     * @param messageFormat messageFormat
     * @param objects objects
     */
    public RoberException(String messageFormat, Object ...objects) {
        this(MessageFormat.format(messageFormat, objects));
    }

    /**
     * @param cause cause
     * @param messageFormat messageFormat
     * @param objects objects
     */
    public RoberException(Throwable cause, String messageFormat, Object ...objects) {
        this(MessageFormat.format(messageFormat, objects),cause);
    }


    /**
     * @param cause cause
     * @param message message
     */
    public RoberException(Throwable cause, String message) {
        super(message, cause);
    }


    /**
     * @param cause cause
     */
    public RoberException(Throwable cause) {
        super(cause);
    }


    /**
     * @param message message
     * @param cause cause
     */
    public RoberException(String message, Throwable cause) {
        super(message, cause);
    }


    /**
     * @param message message
     * @param cause cause
     * @param enableSuppression enableSuppression
     * @param writableStackTrace writableStackTrace
     */
    public RoberException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
