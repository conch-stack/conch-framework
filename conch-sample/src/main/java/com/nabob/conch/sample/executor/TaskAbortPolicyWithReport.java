package com.nabob.conch.sample.executor;

import lombok.extern.slf4j.Slf4j;
import com.nabob.conch.sample.uitl.JVMUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * TaskAbortPolicyWithReport
 *
 * @author Adam
 * @since 2023/3/14
 */
@Slf4j
public class TaskAbortPolicyWithReport extends ThreadPoolExecutor.AbortPolicy {

    private final String threadName;

    /**
     * 最近打印JStack的时间
     */
    private static volatile long lastPrintTime = 0;

    /**
     * 十分钟间隔
     */
    private static final long TEN_MINUTES_MILLS = 10 * 60 * 1000;

    private static final String OS_WIN_PREFIX = "win";
    private static final String OS_NAME_KEY = "os.name";
    private static final String WIN_DATETIME_FORMAT = "yyyy-MM-dd_HH-mm-ss";
    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    /**
     * 信号量
     */
    private static Semaphore guard = new Semaphore(1);

    public TaskAbortPolicyWithReport(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
        String msg = String.format("Thread pool is EXHAUSTED!" +
                        " Thread Name: %s, Pool Size: %d (active: %d, core: %d, max: %d, largest: %d), Task: %d (completed: "
                        + "%d)," +
                        " Executor status:(isShutdown:%s, isTerminated:%s, isTerminating:%s)",
                threadName, e.getPoolSize(), e.getActiveCount(), e.getCorePoolSize(), e.getMaximumPoolSize(),
                e.getLargestPoolSize(),
                e.getTaskCount(), e.getCompletedTaskCount(), e.isShutdown(), e.isTerminated(), e.isTerminating());
        log.warn(msg);
        dumpJStack();
        throw new RejectedExecutionException(msg);
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();

        //dump every 10 minutes
        if (now - lastPrintTime < TEN_MINUTES_MILLS) {
            return;
        }

        if (!guard.tryAcquire()) {
            return;
        }

        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            String dumpPath = System.getProperty("user.home");

            SimpleDateFormat sdf;

            String os = System.getProperty(OS_NAME_KEY).toLowerCase();

            // window system don't support ":" in file name
            if (os.contains(OS_WIN_PREFIX)) {
                sdf = new SimpleDateFormat(WIN_DATETIME_FORMAT);
            } else {
                sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            }

            String dateStr = sdf.format(new Date());
            //try-with-resources
            try (FileOutputStream jStackStream = new FileOutputStream(
                    new File(dumpPath, "Task_JStack.log" + "." + dateStr))) {
                JVMUtil.jstack(jStackStream);
            } catch (Throwable t) {
                log.error("dump jStack error", t);
            } finally {
                guard.release();
            }
            lastPrintTime = System.currentTimeMillis();
        });
        //must shutdown thread pool ,if not will lead to OOM
        pool.shutdown();

    }
}
