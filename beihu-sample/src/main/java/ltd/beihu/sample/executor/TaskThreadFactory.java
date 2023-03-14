package ltd.beihu.sample.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskThreadFactory
 *
 * @author Adam
 * @since 2023/3/13
 */
public class TashThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemon;

    private final int threadPriority;

    @Override
    public Thread newThread(Runnable r) {
        return null;
    }
}
