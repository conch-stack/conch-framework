package ltd.beihu.core.netty.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ltd.beihu.core.netty.common.Operation;
import ltd.beihu.core.netty.common.OperationResult;
import ltd.beihu.core.netty.common.RequestMessage;
import ltd.beihu.core.netty.common.ResponseMessage;

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
