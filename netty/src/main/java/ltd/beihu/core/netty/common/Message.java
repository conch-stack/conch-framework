package ltd.beihu.core.netty.common;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import ltd.beihu.core.netty.util.JsonUtil;

import java.nio.charset.StandardCharsets;

/**
 * 消息
 *
 * @author Adam
 * @since 2022/1/20
 */
@Data
public abstract class Message<T extends MessageBody> {

    /**
     * 消息头
     */
    private MessageHeader messageHeader;

    /**
     * 消息体
     */
    private T messageBody;

    public void encode(ByteBuf byteBuf) {
        byteBuf.writeInt(messageHeader.getVersion());
        byteBuf.writeLong(messageHeader.getStreamId());
        byteBuf.writeInt(messageHeader.getOpCode());
        byteBuf.writeBytes(JsonUtil.toJson(this.messageBody).getBytes(StandardCharsets.UTF_8));
    }

    public void decode(ByteBuf msg) {
        int version = msg.readInt();
        long streamId = msg.readLong();
        int opCode = msg.readInt();

        MessageHeader header = new MessageHeader();
        header.setVersion(version);
        header.setStreamId(streamId);
        header.setOpCode(opCode);
        this.messageHeader = header;

        Class<T> messageBodyDecodeClass = getMessageBodyDecodeClass(opCode);

        this.messageBody = JsonUtil.fromJson(msg.toString(StandardCharsets.UTF_8), messageBodyDecodeClass);
    }

    public abstract Class<T> getMessageBodyDecodeClass(int opCode);

}
