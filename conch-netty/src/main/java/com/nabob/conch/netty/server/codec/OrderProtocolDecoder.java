package com.nabob.conch.netty.server.codec;

import com.nabob.conch.netty.common.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * Order Protocol Decoder
 * <p>
 * 服务端 二次解码器
 * <p>
 * 将 ByteBuf 解析为可识别的结构体 RequestMessage
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.decode(byteBuf);
        out.add(requestMessage);
    }
}
