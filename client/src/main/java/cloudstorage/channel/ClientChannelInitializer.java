package cloudstorage.channel;

import cloudstorage.handler.ClientMessageHandlerFactory;
import io.netty.channel.ChannelHandler;
import util.channel.ConnectionChannelInitializer;

/**
 * The client channels initializing class
 */
public class ClientChannelInitializer extends ConnectionChannelInitializer {
    /**
     * Gets the main {@link ChannelHandler} implementation
     * for {@link cloudstorage.client.Client Client} work.
     *
     * @return the {@link ChannelHandler} implementation
     */
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return ClientMessageHandlerFactory.storageClientMessageHandler();
    }
}
