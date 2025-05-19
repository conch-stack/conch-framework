package com.nabob.conch.sample.enhanceconsumer;

/**
 * EventState
 *
 * @author Adam
 * @since 2025/3/13
 */
public enum EventState {

    PENDING,        // 待处理
    IN_PROGRESS,    // 处理中

    RETRYING,       // 重试中

    SUCCESS,        // 成功
    FAILURE,        // 失败
    ERROR           // 异常

    ;
}
