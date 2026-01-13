package websocket.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (!request.decoderResult().isSuccess()) {
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }

        if (request.method() != HttpMethod.GET) {
            sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }

        if ("/".equals(request.uri()) || "/index.html".equals(request.uri())) {
            URL fileUrl = getClass().getResource("/websocket/WebSocketClient.html");
            if (fileUrl == null) {
                sendHttpResponse(ctx, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND));
                return;
            }
            try (InputStream fileStream = fileUrl.openStream()) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fileStream.read(buffer)) != -1) {
                    baos.write(buffer, 0, length);
                }
                byte[] fileBytes = baos.toByteArray();
                ByteBuf content = Unpooled.copiedBuffer(fileBytes);
                FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);
                res.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
                HttpUtil.setContentLength(res, content.readableBytes());
                sendHttpResponse(ctx, res);
            }
        } else {
            ctx.fireChannelRead(request.retain());
        }
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpResponse res) {
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpUtil.isKeepAlive(res)) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
