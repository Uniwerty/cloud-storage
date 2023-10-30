package cloudstorage.channel;

import cloudstorage.handler.ClientFileUploader;
import cloudstorage.handler.StorageClientMessageHandler;
import common.channel.ChannelManager;
import common.handler.JsonDecoder;
import common.handler.JsonEncoder;
import common.message.ClientMessage;
import common.message.ServerMessage;
import io.netty.channel.ChannelHandler;

public class ClientChannelManager extends ChannelManager {
    @Override
    protected ChannelHandler jsonDecoderImpl() {
        return new JsonDecoder<>(ServerMessage.class);
    }

    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageClientMessageHandler();
    }

    @Override
    protected ChannelHandler jsonEncoderImpl() {
        return new JsonEncoder<>(ClientMessage.class);
    }

    @Override
    protected ChannelHandler fileUploader() {
        return new ClientFileUploader();
    }
}
