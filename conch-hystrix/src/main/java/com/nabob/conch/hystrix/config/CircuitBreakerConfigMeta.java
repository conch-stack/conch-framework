package com.nabob.conch.hystrix.config;

/**
 * 断路器 配置
 *
 * @author Adam
 * @since 2023/9/14
 */
public class CircuitBreakerConfigMeta {

    /**
     * 强制关闭
     */
    private boolean forceClosed;

    /**
     * 休眠窗口时间 单位：毫秒
     */
    private long sleepWindow = 10 * 1000L;

    /**
     * 请求流量阈值
     */
    private long requestVolumeThreshold;

    /**
     * 错误阈值百分百
     */
    private int errorThresholdPercentage;

    public boolean isForceClosed() {
        return forceClosed;
    }

    public long getSleepWindowInMilliseconds() {
        return sleepWindow;
    }

    public long getRequestVolumeThreshold() {
        return requestVolumeThreshold;
    }

    public int getErrorThresholdPercentage() {
        return errorThresholdPercentage;
    }
}
