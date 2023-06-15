package com.nabob.conch.sample.executor;

import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * TaskQueue
 *
 * @author Adam
 * @since 2023/3/13
 */
public class TaskQueue extends LinkedBlockingQueue<Runnable> {

    private TaskThreadPoolExecutor executor;

    public TaskQueue() {
        super();
    }

    public TaskQueue(int capacity) {
        super(capacity);
    }

    public TaskQueue(Collection<? extends Runnable> c) {
        super(c);
    }

    public void setExecutor(TaskThreadPoolExecutor executor) {
        this.executor = executor;
    }

    /**
     * 重新 offer 方法，以控制创建线程逻辑
     */
    @Override
    public boolean offer(Runnable runnable) {
        if (executor == null) {
            throw new RejectedExecutionException("The task queue does not have executor!");
        }

        int currentPoolThreadSize = executor.getPoolSize();
        // 如果有空闲Worker，将任务加到队列里面，等待Worker来处理
        if (executor.getSubmittedTaskCount() < currentPoolThreadSize) {
            return super.offer(runnable);
        }

        // 如果当前线程池的线程数 小于 最大线程数，返回 false，以触发Executor创建新线程（因为我们的队列是不限制容量的，所以需要在适当时候触发新线程的创建）
        if (currentPoolThreadSize < executor.getMaximumPoolSize()) {
            return false;
        }

        // currentPoolThreadSize >= max
        return super.offer(runnable);
    }

    /**
     * 如果触发拒绝策略，尝试继续添加到队列
     */
    public boolean retryOffer(Runnable o, long timeout, TimeUnit unit) throws InterruptedException {
        if (executor.isShutdown()) {
            throw new RejectedExecutionException("Executor is shutdown!");
        }
        return super.offer(o, timeout, unit);
    }
}
