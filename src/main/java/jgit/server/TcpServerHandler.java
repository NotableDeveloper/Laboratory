package jgit.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServerHandler extends SimpleChannelInboundHandler<String> {
    private static final Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client connected: {}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.debug("Received message from {}: {}", ctx.channel().remoteAddress(), msg);

        String response = processRequest(msg);
        ctx.writeAndFlush(response + "\n");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Error in channel: {}", ctx.channel().remoteAddress(), cause);
        ctx.close();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client disconnected: {}", ctx.channel().remoteAddress());
        super.channelInactive(ctx);
    }

    private String processRequest(String request) {
        if (request == null || request.trim().isEmpty()) {
            return "ERROR: Empty request";
        }

        return "RECEIVED: " + request.trim();
    }
}
