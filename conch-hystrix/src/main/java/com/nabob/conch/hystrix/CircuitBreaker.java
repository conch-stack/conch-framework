package com.nabob.conch.hystrix;

import com.nabob.conch.hystrix.config.CircuitBreakerConfigMeta;
import com.nabob.conch.hystrix.core.Status;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 断路器
 *
 * @author Adam
 * @since 2023/9/14
 */
public class CircuitBreaker {

    private String serviceName;

    private String method;

    /**
     * 断路器开始时间
     * <p>
     * default for test
     */
    private AtomicLong circuitOpened;

    /**
     * 断路器状态
     */
    private AtomicReference<Status> status;

    /**
     * 半开通过数量统计
     */
    private AtomicLong halfOpenPassNum;
    /**
     * 最大半开数量
     */
    private long maxHalfOpenPassNum;

    public CircuitBreaker(String serviceName, String method) {
        this.serviceName = serviceName;
        this.method = method;
    }

    /**
     * CLOSE -> OPEN
     */
    public boolean tryMarkOpen(int errorPercentage, Long requestNum) {
        CircuitBreakerConfigMeta configData = getConfig(method);
        if (configData != null && !configData.isForceClosed()
                && requestNum >= configData.getRequestVolumeThreshold()
                && errorPercentage >= configData.getErrorThresholdPercentage()) {
            if (status.compareAndSet(Status.CLOSE, Status.OPEN)) {
                circuitOpened.set(System.currentTimeMillis());
                return true;
            }
        }
        return false;
    }

    /**
     * OPEN -> HALF_OPEN
     */
    public boolean attemptExecution() {
        CircuitBreakerConfigMeta configData = getConfig(method);

        // 如果当前时间超过休眠窗口
        if (isAfterSleepWindow(configData)) {
            if (status.compareAndSet(Status.OPEN, Status.HALF_OPEN)) {
                halfOpenPassNum.set(0);
                return true;
            } else {
                if (halfOpenPassNum.incrementAndGet() > maxHalfOpenPassNum) {
                    return false;
                }
                return true;
            }
        }

        return false;
    }

    /**
     * HALF_OPEN -> OPEN
     */
    public void makeNonSuccess() {
        if (status.compareAndSet(Status.HALF_OPEN, Status.OPEN)) {
            circuitOpened.set(System.currentTimeMillis());
        }
    }

    /**
     * HALF_OPEN -> CLOSE
     */
    public boolean makeSuccess() {
        boolean flag = false;
        if (status.compareAndSet(Status.HALF_OPEN, Status.CLOSE)) {
            circuitOpened.set(-1L);
            flag = true;
        }
        return flag;
    }

    // private

    private boolean isAfterSleepWindow(CircuitBreakerConfigMeta configData) {
        long circuitOpenTime = circuitOpened.get();
        long currentTime = System.currentTimeMillis();
        long sleepWindowTime = configData.getSleepWindowInMilliseconds();

        return currentTime > circuitOpenTime + sleepWindowTime;
    }

    private CircuitBreakerConfigMeta getConfig(String method) {
        return new CircuitBreakerConfigMeta();
    }

}
