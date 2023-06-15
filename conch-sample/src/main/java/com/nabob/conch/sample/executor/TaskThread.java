package com.nabob.conch.sample.executor;

import lombok.extern.slf4j.Slf4j;

/**
 * TaskThread
 *
 * @author Adam
 * @since 2023/3/13
 */
@Slf4j
public class TaskThread extends Thread {

    private final long creationTime;

    public TaskThread(ThreadGroup group, Runnable target, String name) {
        super(group, new ErrorSupportRunnable(target), name);
        this.creationTime = System.currentTimeMillis();
    }

    public TaskThread(ThreadGroup group, Runnable target, String name, long stackSize) {
        super(group, new ErrorSupportRunnable(target), name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }

    public long getCreationTime() {
        return creationTime;
    }

    private static class ErrorSupportRunnable implements Runnable {
        private final Runnable wrappedRunnable;

        ErrorSupportRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }

        @Override
        public void run() {
            try {
                wrappedRunnable.run();
            } catch (TaskThreadException taskThreadException) {
                if (taskThreadException.isNeedThrow()) {
                    throw taskThreadException;
                }
                log.warn("ErrorSupportRunnable-taskThreadException", taskThreadException);
            } catch (Throwable e) {
                log.warn("ErrorSupportRunnable-e", e);
                throw e;
            }
        }
    }
}
