package cloudstorage.response;

import common.channel.ChannelManager;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

public interface ResponseHandler {
    AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    AttributeKey<Boolean> NEED_CONFIRM_KEY = AttributeKey.valueOf("needConfirm");

    void handle(Channel channel) throws Exception;
}
