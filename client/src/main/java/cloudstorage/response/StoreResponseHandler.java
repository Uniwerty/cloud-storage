package cloudstorage.response;

import common.channel.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class StoreResponseHandler implements ResponseHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreResponseHandler.class);

    @Override
    public void handle(Channel channel) throws IOException, InterruptedException {
        ChannelManager channelManager = channel.attr(MANAGER_KEY).get();
        channelManager.setFileDownloadHandlers(channel);
        ChannelPromise onWritePromise = channel.newPromise();
        String filepath = channel.attr(FILE_KEY).get();
        channel.writeAndFlush(Files.readAllBytes(Path.of(filepath)), onWritePromise);
        onWritePromise.sync();
        channel.attr(NEED_CONFIRM_KEY).set(false);
        logger.info("Sent {} to the server", filepath);
        channelManager.setStandardHandlers(channel);
    }
}
