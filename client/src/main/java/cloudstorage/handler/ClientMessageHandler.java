package cloudstorage.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientMessageHandler extends SimpleChannelInboundHandler<String> {
    protected static final Logger logger = LoggerFactory.getLogger(ClientMessageHandler.class);

    /**
     * Handles a channel register.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @throws Exception if an error occurred during handling
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected to the server");
    }

    /**
     * Handles a caught exception.
     *
     * @param ctx   {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param cause the {@link Throwable} caught
     * @throws Exception if an error occurred during handling
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception occurred: {}", cause.getMessage());
    }
}
