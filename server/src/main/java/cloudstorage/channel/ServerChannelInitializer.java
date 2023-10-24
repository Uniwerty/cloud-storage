package cloudstorage.channel;

import cloudstorage.handler.StorageServerMessageHandler;
import common.channel.ConnectionChannelInitializer;
import common.handler.JsonDecoder;
import common.handler.JsonEncoder;
import common.message.ClientCommand;
import common.message.ServerResponse;
import io.netty.channel.ChannelHandler;

public class ServerChannelInitializer extends ConnectionChannelInitializer {
    @Override
    protected ChannelHandler jsonDecoderImpl() {
        return new JsonDecoder<>(ClientCommand.class);
    }

    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageServerMessageHandler();
    }

    @Override
    protected ChannelHandler jsonEncoderImpl() {
        return new JsonEncoder<>(ServerResponse.class);
    }
}
