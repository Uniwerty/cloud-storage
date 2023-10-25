package cloudstorage.channel;

import cloudstorage.handler.ClientFileLoader;
import cloudstorage.handler.StorageClientMessageHandler;
import common.channel.ChannelManager;
import common.handler.JsonDecoder;
import common.handler.JsonEncoder;
import common.message.ClientCommand;
import common.message.ServerResponse;
import io.netty.channel.ChannelHandler;

public class ClientChannelManager extends ChannelManager {
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

    @Override
    protected ChannelHandler fileUploader() {
        return new ClientFileLoader();
    }
}
