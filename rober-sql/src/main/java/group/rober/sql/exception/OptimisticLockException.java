package group.rober.sql.exception;

import group.rober.runtime.lang.RoberException;

/**
 * 乐观锁异常
 * Created by tisir yangsong158@qq.com on 2017-06-04
 */
public class OptimisticLockException extends RoberException {
    private static final long serialVersionUID = 6347385927109725125L;

    public OptimisticLockException() {
    }

    public OptimisticLockException(String message) {
        super(message);
    }

    public OptimisticLockException(String messageFormat, Object... objects) {
        super(messageFormat, objects);
    }

    public OptimisticLockException(Throwable cause, String messageFormat, Object... objects) {
        super(cause, messageFormat, objects);
    }

    public OptimisticLockException(Throwable cause, String message) {
        super(cause, message);
    }

    public OptimisticLockException(Throwable cause) {
        super(cause);
    }

    public OptimisticLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public OptimisticLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
