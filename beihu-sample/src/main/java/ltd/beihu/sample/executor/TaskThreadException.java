package ltd.beihu.sample.executor;

/**
 * TaskThreadException
 *
 * @author Adam
 * @since 2023/3/13
 */
public class TaskThreadException extends RuntimeException {

    private boolean needThrow;

    public TaskThreadException(boolean needThrow) {
        this.needThrow = needThrow;
    }

    public TaskThreadException(String message, boolean needThrow) {
        super(message);
        this.needThrow = needThrow;
    }

    public TaskThreadException(String message, Throwable cause, boolean needThrow) {
        super(message, cause);
        this.needThrow = needThrow;
    }

    public TaskThreadException(Throwable cause, boolean needThrow) {
        super(cause);
        this.needThrow = needThrow;
    }

    public TaskThreadException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, boolean needThrow) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.needThrow = needThrow;
    }

    public boolean isNeedThrow() {
        return needThrow;
    }
}
