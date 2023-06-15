package com.nabob.conch.sample.timelimit;

import com.alibaba.ttl.TtlRunnable;

import java.util.Objects;
import java.util.Optional;

/**
 * @author Adam
 * @since 2022/11/18
 */
public class TestConsumer {

    private Thread thread;
    private EventTask task;

    private TestConsumer() {
    }

    public void start() {
        this.thread.start();
    }

    public void stop() {
        this.task.stop();
    }

    public static Optional<TestConsumer> getInstance(String key) {
        TestConsumer rs = new TestConsumer();
        try {
            Consumer consumer = new Consumer(key);
            rs.task = new EventTask(consumer);
            TtlRunnable ttlRunnable = TtlRunnable.get(rs.task);
            rs.thread = new Thread(ttlRunnable);
            return Optional.of(rs);
        } catch (Exception e) {
            if (Objects.nonNull(rs.thread) && Objects.nonNull(rs.task)) {
                rs.task.stop();
            }
            return Optional.empty();
        }
    }

    static class EventTask implements Runnable {

        private Consumer consumer;
        private volatile boolean stop = false;

        public EventTask(Consumer consumer) {
            this.consumer = consumer;
        }

        @Override
        public void run() {
            while (!stop) {
                try {
                    consumer.consume(); // 具体的consumer可用池进行管理
                } catch (Exception e) {
                    break;
                }
            }
            System.err.println("我被销毁了" + consumer.getKey());
        }

        public void stop() {
            this.stop = true;
        }
    }
}
