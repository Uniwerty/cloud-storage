package cloudstorage.handler;

import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import common.message.ServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLoaderHandler extends SimpleChannelInboundHandler<byte[]> {
    private static final AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");
    private static final AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    private static final AttributeKey<String> USER_KEY = AttributeKey.valueOf("user");
    private static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    private static final Logger logger = LoggerFactory.getLogger(FileLoaderHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] fileBytes) throws Exception {
        Channel channel = ctx.channel();
        String login = channel.attr(USER_KEY).get();
        if (channel.attr(AUTH_KEY).get().isUserAuthorized(login)) {
            String filepath = channel.attr(FILE_KEY).get();
            channel.attr(STORAGE_KEY).get().storeFile(login, filepath, fileBytes);
            logger.info("Stored {} from {} successfully", filepath, login);
            channel.writeAndFlush(new ServerResponse(true, "Stored successfully"));
        } else {
            logger.info("Ignored data from unauthorized client");
            channel.writeAndFlush(new ServerResponse(false, "The data was ignored"));
        }
        channel.pipeline().remove(ctx.name());
        channel.pipeline().remove("bytesDecoder");
        ctx.fireChannelReadComplete();
    }
}
