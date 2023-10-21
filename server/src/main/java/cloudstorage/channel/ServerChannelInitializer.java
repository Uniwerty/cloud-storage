package cloudstorage.channel;

import cloudstorage.handler.ServerMessageHandlerFactory;
import io.netty.channel.ChannelHandler;
import common.channel.ConnectionChannelInitializer;

public class ServerChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return ServerMessageHandlerFactory.storageServerMessageHandler();
    }
}
