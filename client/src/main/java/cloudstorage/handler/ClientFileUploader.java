package cloudstorage.handler;

import common.channel.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * The class for uploading file from server to client
 */
public class ClientFileUploader extends SimpleChannelInboundHandler<ByteBuf> {
    private static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    private static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    private static final AttributeKey<Long> FILE_SIZE_KEY = AttributeKey.valueOf("fileSize");
    private static final Logger logger = LoggerFactory.getLogger(ClientFileUploader.class);
    private long bytesRead = 0;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf fileChunk) throws Exception {
        Channel channel = ctx.channel();
        String filePath = channel.attr(FILE_KEY).get();
        try (OutputStream fileOutput =
                     Files.newOutputStream(
                             Path.of(filePath),
                             StandardOpenOption.CREATE,
                             StandardOpenOption.APPEND
                     )
        ) {
            int readableBytes = fileChunk.readableBytes();
            fileChunk.readBytes(fileOutput, readableBytes);
            bytesRead += readableBytes;
        }
        if (bytesRead == channel.attr(FILE_SIZE_KEY).get()) {
            channel.attr(MANAGER_KEY).get().setStandardHandlers(channel);
            logger.info("File loaded successfully to {}", filePath);
        }
    }
}
