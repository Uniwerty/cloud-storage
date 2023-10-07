package cloudstorage.handler;

import io.netty.channel.ChannelHandlerContext;

/**
 * The {@link ClientMessageHandler} implementation to work with cloud storage server
 */
public class StorageClientMessageHandler extends ClientMessageHandler {
    /**
     * Handles a message {@code String} read from {@code Channel}.
     *
     * @param ctx the {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param msg the message {@code String} to handle
     * @throws Exception if an error occurred during handling
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("Server response: {}", msg);
    }
}
