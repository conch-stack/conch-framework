package ltd.beihu.core.netty.client.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Order Frame Encoder
 * <p>
 * 客户端 二次编码器
 * <p>
 * 为让对端可对 接收的TCP数据 进行依据Length解决 粘包半包问题
 * 将客户端发送的数据包增加Length协议支持
 *
 * @author Adam
 * @since 2022/1/21
 */
public class OrderFrameEncoder extends LengthFieldPrepender {

    /**
     * lengthFieldLength 长度字段的长度
     */
    public OrderFrameEncoder() {
        super(2);
    }
}
