package com.nabob.conch.hystrix.core;

/**
 * 断路器 状态
 *
 * @author Adam
 * @since 2023/9/14
 */
public enum Status {
    CLOSE, OPEN, HALF_OPEN
}
