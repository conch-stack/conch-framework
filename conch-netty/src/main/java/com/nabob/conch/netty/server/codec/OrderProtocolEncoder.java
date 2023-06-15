package com.nabob.conch.netty.server.codec;

import com.nabob.conch.netty.common.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * Order Protocol Encoder
 * <p>
 * 服务端 一次编码器
 * <p>
 * 将 ResponseMessage 编码为可传输的 ByteBuf
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderProtocolEncoder extends MessageToMessageEncoder<ResponseMessage> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage responseMessage, List<Object> out) throws Exception {
        // 申请一个 ByteBuf
        ByteBuf buffer = channelHandlerContext.alloc().buffer();
        // encode
        responseMessage.encode(buffer);
        // 将 ByteBuf 送出去
        out.add(buffer);
    }
}
