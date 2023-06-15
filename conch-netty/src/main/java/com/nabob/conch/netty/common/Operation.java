package com.nabob.conch.netty.common;

/**
 * Operation
 *
 * @author Adam
 * @since 2022/1/20
 */
public abstract class Operation extends MessageBody {

    public abstract OperationResult execute();
}
