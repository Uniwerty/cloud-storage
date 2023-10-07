package cloudstorage.channel;

import cloudstorage.handler.ServerMessageHandlerFactory;
import io.netty.channel.ChannelHandler;
import util.channel.ConnectionChannelInitializer;

/**
 * The server channel initializing class
 */
public class ServerChannelInitializer extends ConnectionChannelInitializer {
    /**
     * Gets the main {@link ChannelHandler} implementation
     * for {@link cloudstorage.server.Server Server} work.
     *
     * @return the {@link ChannelHandler} implementation
     */
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return ServerMessageHandlerFactory.storageServerMessageHandler();
    }
}
