package cloudstorage.handler;

import common.channel.ChannelManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The class for uploading file from server to client
 */
public class ClientFileUploader extends SimpleChannelInboundHandler<byte[]> {
    private static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    private static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    private static final Logger logger = LoggerFactory.getLogger(ClientFileUploader.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] fileBytes) throws Exception {
        Channel channel = ctx.channel();
        String filepath = channel.attr(FILE_KEY).get();
        Files.write(Path.of(filepath), fileBytes);
        logger.info("File loaded successfully to {}", filepath);
        channel.attr(MANAGER_KEY).get().setStandardHandlers(channel);
    }
}
