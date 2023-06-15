package com.nabob.conch.netty.common;

/**
 * @author Adam
 * @since 2022/1/20
 */
public class ResponseMessage extends Message<OperationResult>{

    @Override
    public Class getMessageBodyDecodeClass(int opCode) {
        return OperationType.fromOpCode(opCode).getOperationResultClass();
    }
}
