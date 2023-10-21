package cloudstorage.channel;

import cloudstorage.handler.ClientMessageHandlerFactory;
import io.netty.channel.ChannelHandler;
import common.channel.ConnectionChannelInitializer;

public class ClientChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return ClientMessageHandlerFactory.storageClientMessageHandler();
    }
}
