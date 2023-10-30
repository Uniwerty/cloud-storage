package cloudstorage.response;

import common.channel.ChannelManager;
import common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public interface ResponseHandler {
    AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    AttributeKey<Long> FILE_SIZE_KEY = AttributeKey.valueOf("fileSize");
    AttributeKey<Boolean> NEED_CONFIRM_KEY = AttributeKey.valueOf("needConfirm");

    void handle(Channel channel, ServerMessage message) throws Exception;

    default void handle(Channel channel) throws Exception {
        handle(channel, null);
    }
}
