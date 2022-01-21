package ltd.beihu.core.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ltd.beihu.core.netty.client.codec.OrderFrameDecoder;
import ltd.beihu.core.netty.client.codec.OrderFrameEncoder;
import ltd.beihu.core.netty.client.codec.OrderProtocolDecoder;
import ltd.beihu.core.netty.client.codec.OrderProtocolEncoder;
import ltd.beihu.core.netty.common.RequestMessage;
import ltd.beihu.core.netty.common.order.OrderOperation;
import ltd.beihu.core.netty.util.IdUtil;

import java.util.concurrent.ExecutionException;

/**
 * Netty Client V1
 * <p>
 * 最简单的Client
 *
 * @author Adam
 * @since 2022/1/22
 */
public class NettyClientV1 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        Bootstrap bootstrap = new Bootstrap();

        // 设置Client Channel 的 IO 模式
        bootstrap.channel(NioSocketChannel.class);

        // 设置Client的Reactor线程模型
        bootstrap.group(new NioEventLoopGroup());

        // 设置工作线程的Handler
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                ChannelPipeline pipeline = nioSocketChannel.pipeline();

                // 添加 pipeline 的 Handler 链

                // Child Log Handler
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                // 注意 ">" 形顺序
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());

            }
        });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();

        // 发送测试数据
        RequestMessage firstTestRequestMessage = new RequestMessage(IdUtil.nextId(), new OrderOperation(1, "first test"));

        channelFuture.channel().writeAndFlush(firstTestRequestMessage);

        channelFuture.channel().closeFuture().get();
    }

}
