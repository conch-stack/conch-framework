package ltd.beihu.core.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import ltd.beihu.core.netty.server.codec.OrderFrameDecoder;
import ltd.beihu.core.netty.server.codec.OrderFrameEncoder;
import ltd.beihu.core.netty.server.codec.OrderProtocolDecoder;
import ltd.beihu.core.netty.server.codec.OrderProtocolEncoder;
import ltd.beihu.core.netty.server.handler.OrderServerProcessHandler;

import java.util.concurrent.ExecutionException;

/**
 * Netty Server
 *
 * @author Adam
 * @since 2022/1/21
 */
public class NettyServer12 {

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        // 设置Server Channel 的 IO 模式
        serverBootstrap.channel(NioServerSocketChannel.class);

        // 添加日志 - 全局的
        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        // 设置Server的Reactor线程模型
        serverBootstrap.group(new NioEventLoopGroup());

        // 设置工作线程的Handler
        serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {
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

                // 业务处理 Handler
                pipeline.addLast(new OrderServerProcessHandler());

            }
        });

        ChannelFuture channelFuture = serverBootstrap.bind(8090).sync();
        channelFuture.channel().closeFuture().get();
    }

}
