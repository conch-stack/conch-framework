package ltd.beihu.core.netty.client.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import ltd.beihu.core.netty.common.Operation;
import ltd.beihu.core.netty.common.RequestMessage;
import ltd.beihu.core.netty.util.IdUtil;

import java.util.List;

/**
 * Operation To RequestMessage Encoder
 *
 * @author Adam
 * @since 2022/1/22
 */
public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        out.add(requestMessage);
    }
}
