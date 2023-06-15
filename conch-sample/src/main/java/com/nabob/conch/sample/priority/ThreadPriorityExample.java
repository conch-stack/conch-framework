package com.nabob.conch.sample.priority;

import java.util.stream.IntStream;

/**
 * Java程序中对线程所设置的优先级只是给操作系统一个建议，操作系统不一定会采纳。而真正的调用顺序，是由操作系统的线程调度算法决定的
 * <p>
 * Java提供一个线程调度器来监视和控制处于RUNNABLE状态的线程。线程的调度策略采用抢占式，优先级高的线程比优先级低的线程会有更大的几率优先执行。
 * 在优先级相同的情况下，按照“先到先得”的原则。
 * 每个Java程序都有一个默认的主线程，就是通过JVM启动的第一个线程main线程。
 * 还有一种线程称为守护线程（Daemon），守护线程默认的优先级比较低。
 */
public class ThreadPriorityExample {

    public static class T1 extends Thread {
        @Override
        public void run() {
            super.run();
            System.out.println(String.format("当前执行的线程是：%s，优先级：%d",
                    Thread.currentThread().getName(),
                    Thread.currentThread().getPriority()));
        }
    }

    public static void main(String[] args) {
        IntStream.range(1, 10).forEach(i -> {
            Thread thread = new Thread(new T1());
            thread.setPriority(i);
            thread.start();
        });
    }
}