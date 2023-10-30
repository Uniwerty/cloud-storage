package cloudstorage.channel;

import cloudstorage.handler.ServerFileUploader;
import cloudstorage.handler.StorageServerMessageHandler;
import common.channel.ChannelManager;
import common.handler.JsonDecoder;
import common.handler.JsonEncoder;
import common.message.ClientMessage;
import common.message.ServerMessage;
import io.netty.channel.ChannelHandler;

public class ServerChannelManager extends ChannelManager {
    @Override
    protected ChannelHandler jsonDecoderImpl() {
        return new JsonDecoder<>(ClientMessage.class);
    }

    @Override
    protected ChannelHandler mainChannelHandlerImpl() {
        return new StorageServerMessageHandler();
    }

    @Override
    protected ChannelHandler jsonEncoderImpl() {
        return new JsonEncoder<>(ServerMessage.class);
    }

    @Override
    protected ChannelHandler fileUploader() {
        return new ServerFileUploader();
    }
}
