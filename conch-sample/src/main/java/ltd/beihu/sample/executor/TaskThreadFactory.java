package ltd.beihu.sample.executor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskThreadFactory
 *
 * @author Adam
 * @since 2023/3/13
 */
public class TaskThreadFactory implements ThreadFactory {

    private final ThreadGroup group;

    private final AtomicInteger threadNumber = new AtomicInteger(1);

    private final String prefix;

    private final boolean daemon;

    private final int threadPriority;

    public TaskThreadFactory(String prefix, boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        this.group = (s == null) ? Thread.currentThread().getThreadGroup() : s.getThreadGroup();
        this.prefix = prefix;
        this.daemon = daemon;
        this.threadPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        TaskThread taskThread = new TaskThread(group, r, prefix + threadNumber.getAndIncrement());
        taskThread.setDaemon(daemon);
        taskThread.setPriority(threadPriority);
        return taskThread;
    }
}
