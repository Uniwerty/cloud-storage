package cloudstorage.response;

import common.channel.ChannelManager;
import common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.handler.stream.ChunkedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * The class for downloading file to server after confirmation
 */
public class StoreResponseHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreResponseHandler.class);

    @Override
    public void handle(Channel channel, ServerMessage response) throws IOException, InterruptedException {
        ChannelManager channelManager = channel.attr(MANAGER_KEY).get();
        channelManager.setFileDownloadHandlers(channel);
        String filepath = channel.attr(FILE_KEY).get();
        ChannelPromise onWritePromise = channel.newPromise();
        channel.writeAndFlush(new ChunkedFile(new File(filepath)), onWritePromise);
        onWritePromise.sync();
        channel.attr(NEED_CONFIRM_KEY).set(false);
        logger.info("Sent {} to the server", filepath);
        channelManager.setStandardHandlers(channel);
    }
}
