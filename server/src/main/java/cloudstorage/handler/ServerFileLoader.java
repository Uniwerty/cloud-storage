package cloudstorage.handler;

import cloudstorage.service.StorageService;
import common.channel.ChannelManager;
import common.command.Command;
import common.message.ServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerFileLoader extends SimpleChannelInboundHandler<byte[]> {
    private static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    private static final AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    private static final AttributeKey<String> USER_KEY = AttributeKey.valueOf("user");
    private static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    private static final Logger logger = LoggerFactory.getLogger(ServerFileLoader.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, byte[] fileBytes) throws Exception {
        Channel channel = ctx.channel();
        String login = channel.attr(USER_KEY).get();
        String filepath = channel.attr(FILE_KEY).get();
        channel.attr(STORAGE_KEY).get().storeFile(login, filepath, fileBytes);
        logger.info("Stored {} from {} successfully", filepath, login);
        channel.attr(MANAGER_KEY).get().setStandardHandlers(channel);
        channel.writeAndFlush(new ServerResponse(true, Command.STORE.getName(), "Stored successfully"));
    }
}
