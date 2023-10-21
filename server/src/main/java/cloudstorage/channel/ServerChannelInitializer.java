package cloudstorage.channel;

import cloudstorage.handler.StorageServerMessageHandler;
import common.channel.ConnectionChannelInitializer;
import io.netty.channel.ChannelHandler;

public class ServerChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageServerMessageHandler();
    }
}
