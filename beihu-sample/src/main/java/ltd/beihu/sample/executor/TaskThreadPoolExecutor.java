package ltd.beihu.sample.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TaskThreadPoolExecutor
 *
 * @author Adam
 * @since 2023/3/13
 */
public class TaskThreadPoolExecutor extends ThreadPoolExecutor {

    private final AtomicInteger submittedTaskCount = new AtomicInteger(0);

    public TaskThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public int getSubmittedTaskCount() {
        return submittedTaskCount.get();
    }

    @Override
    public void execute(Runnable command) {
        if (command == null) {
            throw new NullPointerException();
        }

        submittedTaskCount.incrementAndGet();

        try {
            super.execute(command);
        } catch (RejectedExecutionException rx) {
            final TaskQueue taskQueue = (TaskQueue) getQueue();
            try {
                if (!taskQueue.retryOffer(command, 0, TimeUnit.MILLISECONDS)) {
                    submittedTaskCount.decrementAndGet();
                    throw new RejectedExecutionException("Queue capacity is full.", rx);
                }
            } catch (InterruptedException ix) {
                submittedTaskCount.decrementAndGet();
                throw new RejectedExecutionException(ix);
            }
        } catch (Throwable t) {
            submittedTaskCount.decrementAndGet();
            throw t;
        }
    }
}

