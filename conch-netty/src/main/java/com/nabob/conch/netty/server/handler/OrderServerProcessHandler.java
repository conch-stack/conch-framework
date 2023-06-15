package com.nabob.conch.netty.server.handler;

import com.nabob.conch.netty.common.Operation;
import com.nabob.conch.netty.common.OperationResult;
import com.nabob.conch.netty.common.RequestMessage;
import com.nabob.conch.netty.common.ResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Order Server Process Handler
 * <p>
 * 对 Channel Inbound 事件的处理
 * <p>
 * 处理Server端业务逻辑
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage) throws Exception {
        // 获取请求操作
        Operation operation = requestMessage.getMessageBody();
        // 执行操作
        OperationResult operationResult = operation.execute();

        // 返回结果
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        // channel write and flush
        channelHandlerContext.writeAndFlush(responseMessage);
    }
}
