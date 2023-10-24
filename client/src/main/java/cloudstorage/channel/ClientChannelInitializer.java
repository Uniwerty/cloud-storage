package cloudstorage.channel;

import cloudstorage.handler.StorageClientMessageHandler;
import common.channel.ConnectionChannelInitializer;
import common.handler.JsonDecoder;
import common.handler.JsonEncoder;
import common.message.ClientCommand;
import common.message.ServerResponse;
import io.netty.channel.ChannelHandler;

public class ClientChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler jsonDecoderImpl() {
        return new JsonDecoder<>(ServerResponse.class);
    }

    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageClientMessageHandler();
    }

    @Override
    protected ChannelHandler jsonEncoderImpl() {
        return new JsonEncoder<>(ClientCommand.class);
    }
}
