package com.nabob.conch.netty.client.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import com.nabob.conch.netty.common.Operation;
import com.nabob.conch.netty.common.RequestMessage;
import com.nabob.conch.netty.util.IdUtil;

import java.util.List;

/**
 * Operation To RequestMessage Encoder
 *
 * @author Adam
 * @since 2022/1/22
 */
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        out.add(requestMessage);
    }
}
