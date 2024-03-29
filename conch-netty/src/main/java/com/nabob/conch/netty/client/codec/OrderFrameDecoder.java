package com.nabob.conch.netty.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * Order Frame Decoder
 * <p>
 * 客户端 一次解码器
 * <p>
 * 处理 客户端 接收TCP数据包 的 粘包半包问题
 * 依据 服务端 传递的Frame里面的Length进行处理
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * maxFrameLength 最大长度
     * lengthFieldOffset 长度字段的位移
     * lengthFieldLength 长度字段的长度
     * lengthAdjustment 是否需要调整Length
     * initialBytesToStrip 是否需要去除头字段
     */
    public OrderFrameDecoder() {
        super(Integer.MAX_VALUE, 0, 2, 0, 2);
    }
}
