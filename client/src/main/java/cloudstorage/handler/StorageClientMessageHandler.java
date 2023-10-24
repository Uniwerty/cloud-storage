package cloudstorage.handler;

import common.message.ServerResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageClientMessageHandler extends SimpleChannelInboundHandler<ServerResponse> {
    protected static final Logger logger = LoggerFactory.getLogger(StorageClientMessageHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerResponse msg) throws Exception {
        logger.info("Server response: {}", msg.message());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected to the server");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception occurred: {}", cause.getMessage());
    }
}
