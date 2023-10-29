package cloudstorage.response;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadResponseHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadResponseHandler.class);

    @Override
    public void handle(Channel channel) {
        channel.attr(MANAGER_KEY).get().setFileUploadHandlers(channel);
        channel.attr(NEED_CONFIRM_KEY).set(false);
        logger.info("Ready to file uploading");
    }
}
