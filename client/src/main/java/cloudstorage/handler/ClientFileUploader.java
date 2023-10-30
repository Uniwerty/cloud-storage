package cloudstorage.handler;

import common.handler.FileUploader;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The class for uploading file from server to client
 */
public class ClientFileUploader extends FileUploader {
    private static final Logger logger = LoggerFactory.getLogger(ClientFileUploader.class);

    @Override
    protected void uploadFileChunk(Channel channel, ByteBuf fileChunk) throws IOException {
        try (OutputStream fileOutput =
                     Files.newOutputStream(
                             Path.of(channel.attr(FILE_KEY).get()),
                             StandardOpenOption.CREATE,
                             StandardOpenOption.APPEND
                     )
        ) {
            int readableBytes = fileChunk.readableBytes();
            fileChunk.readBytes(fileOutput, readableBytes);
            bytesRead += readableBytes;
        }
    }

    @Override
    protected void completeUpload(Channel channel) {
        channel.attr(MANAGER_KEY).get().setStandardHandlers(channel);
        logger.info("File loaded successfully to {}", channel.attr(FILE_KEY).get());
    }
}
