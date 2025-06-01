package cloudstorage.channel;

import cloudstorage.handler.StorageClientMessageHandler;
import common.channel.ConnectionChannelInitializer;
import io.netty.channel.ChannelHandler;

public class ClientChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageClientMessageHandler();
    }
}
