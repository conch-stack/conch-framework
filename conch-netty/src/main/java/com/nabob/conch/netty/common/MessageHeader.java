package ltd.beihu.core.netty.common;

import lombok.Data;

/**
 * 消息头
 *
 * @author Adam
 * @since 2022/1/20
 */
@Data
public class MessageHeader {

    /**
     * 协议版本
     */
    private int version = 1;

    /**
     * 操作类型
     * <p>
     * - 对应后续Message Body的二次编解码
     */
    private int opCode;

    /**
     * 请求唯一id
     * <p>
     * - 用于请求分发，IdMapping跟踪
     */
    private long streamId;

}
