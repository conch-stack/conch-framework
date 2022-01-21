package ltd.beihu.core.netty.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import ltd.beihu.core.netty.common.RequestMessage;
import ltd.beihu.core.netty.common.ResponseMessage;

import java.util.List;

/**
 * Order Protocol Encoder
 * <p>
 * 客户端 一次编码器
 * <p>
 * 将 RequestMessage 编码为可传输的 ByteBuf
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderProtocolEncoder extends MessageToMessageEncoder<RequestMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage requestMessage, List<Object> out) throws Exception {
        // 申请一个 ByteBuf
        ByteBuf buffer = channelHandlerContext.alloc().buffer();
        // encode
        requestMessage.encode(buffer);
        // 将 ByteBuf 送出去
        out.add(buffer);
    }
}
