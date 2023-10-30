package cloudstorage.handler;

import cloudstorage.service.StorageService;
import common.command.Command;
import common.handler.FileUploader;
import common.message.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * The class for uploading file from client to server
 */
public class ServerFileUploader extends FileUploader {
    private static final AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    private static final AttributeKey<String> USER_KEY = AttributeKey.valueOf("user");
    private static final Logger logger = LoggerFactory.getLogger(ServerFileUploader.class);

    @Override
    protected void uploadFileChunk(Channel channel, ByteBuf fileChunk) throws IOException {
        String login = channel.attr(USER_KEY).get();
        String filePath = channel.attr(FILE_KEY).get();
        bytesRead += channel.attr(STORAGE_KEY).get().storeFileChunk(login, filePath, fileChunk);
    }

    @Override
    protected void completeUpload(Channel channel) {
        channel.attr(MANAGER_KEY).get().setStandardHandlers(channel);
        channel.writeAndFlush(new ServerMessage(true, Command.STORE.getName(), "Stored successfully"));
        logger.info("Stored {} from {} successfully", channel.attr(FILE_KEY), channel.attr(USER_KEY));
    }
}
