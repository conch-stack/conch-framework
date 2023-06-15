package com.nabob.conch.netty.common;

/**
 * @author Adam
 * @since 2022/1/20
 */
public class RequestMessage extends Message<Operation> {

    @Override
    public Class getMessageBodyDecodeClass(int opCode) {
        return OperationType.fromOpCode(opCode).getOperationClazz();
    }

    public RequestMessage() {
    }

    public RequestMessage(Long streamId, Operation operation) {
        MessageHeader header = new MessageHeader();
        header.setOpCode(OperationType.fromOperation(operation).getOpCode());
        header.setStreamId(streamId);

        this.setMessageHeader(header);
        this.setMessageBody(operation);
    }
}
