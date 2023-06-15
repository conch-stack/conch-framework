package ltd.beihu.core.netty.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import ltd.beihu.core.netty.common.RequestMessage;
import ltd.beihu.core.netty.common.ResponseMessage;

import java.util.List;

/**
 * Order Protocol Decoder
 * <p>
 * 客户端 二次解码器
 * <p>
 * 将 ByteBuf 解析为可识别的结构体 RequestMessage
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.decode(byteBuf);
        out.add(responseMessage);
    }
}
