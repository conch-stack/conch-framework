package ltd.beihu.sample.executor;

import lombok.Data;
import org.apache.tomcat.util.threads.TaskQueue;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool
 *
 * @author Adam
 * @since 2023/3/13
 */
@Data
public class ThreadPool {

    /**
     * Default thread priority
     */
    protected int threadPriority = Thread.NORM_PRIORITY;

    /**
     * Run threads in daemon or non-daemon state
     */
    protected boolean daemon = true;

    /**
     * Default name prefix for the thread name
     */
    protected String namePrefix = "task-exec-";

    /**
     * max number of threads
     */
    protected int maxThreads = 200;

    /**
     * min number of threads
     */
    protected int minSpareThreads = 25;

    /**
     * idle time in milliseconds
     */
    protected int maxIdleTime = 60000;

    /**
     * The maximum number of elements that can queue up before we reject them
     */
    protected int maxQueueSize = Integer.MAX_VALUE;

    public ThreadPool() {
    }

    public Executor getExecutor(String name, boolean preStart) {
        String threadName = namePrefix + name;
        TaskQueue taskqueue = new TaskQueue(maxQueueSize);
        TaskThreadFactory tf = new TaskThreadFactory(threadName, daemon, threadPriority);
        TaskThreadPoolExecutor executor = new TaskThreadPoolExecutor(minSpareThreads, maxThreads, maxIdleTime, TimeUnit.MILLISECONDS, taskqueue, tf, new TaskAbortPolicyWithReport(threadName));
        if (preStart) {
            executor.prestartAllCoreThreads();
        }
        return executor;
    }
}
