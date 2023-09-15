package com.nabob.conch.hystrix.core;

import lombok.Data;
import org.checkerframework.checker.units.qual.A;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 桶
 * 
 * @author Adam
 * @since 2023/9/14
 */
@Data
public class Bucket {

    /**
     * 窗口开始时间
     */
    private final long windowStart;

    /**
     * 成功数量
     */
    private AtomicInteger successNum;

    /**
     * 失败数量
     */
    private AtomicInteger failNum;

    /**
     * 超时数量
     */
    private AtomicInteger timeoutNum;

    public Bucket(long windowStart) {
        this.windowStart = windowStart;
        this.successNum = new AtomicInteger();
        this.failNum = new AtomicInteger();
        this.timeoutNum = new AtomicInteger();
    }
}
