package jgit.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerBootstrap.class);

    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ChannelFuture channelFuture;

    public TcpServerBootstrap(int port) {
        this.port = port;
    }

    public void start() throws InterruptedException {
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                    .addLast(new StringEncoder(CharsetUtil.UTF_8))
                                    .addLast(new GitCommandHandler());
                        }
                    });

            channelFuture = bootstrap.bind(port).sync();
            logger.info("TCP Server started on port: {}", port);
        } catch (Exception e) {
            logger.error("Failed to start TCP server", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            if (channelFuture != null) {
                channelFuture.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            logger.error("Error closing channel", e);
            Thread.currentThread().interrupt();
        } finally {
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
            logger.info("TCP Server stopped");
        }
    }

    public void awaitTermination() throws InterruptedException {
        if (channelFuture != null) {
            channelFuture.channel().closeFuture().sync();
        }
    }
}
