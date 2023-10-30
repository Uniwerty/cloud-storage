package cloudstorage.response;

import common.message.ServerMessage;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class for preparing client handlers to upload file from server
 */
public class LoadResponseHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadResponseHandler.class);

    @Override
    public void handle(Channel channel, ServerMessage response) {
        channel.attr(MANAGER_KEY).get().setFileUploadHandlers(channel);
        channel.attr(NEED_CONFIRM_KEY).set(false);
        channel.attr(FILE_SIZE_KEY).set(response.fileSize());
        logger.info("Ready to file uploading");
    }
}
