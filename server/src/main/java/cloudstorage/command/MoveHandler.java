package cloudstorage.command;

import cloudstorage.service.StorageService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cloudstorage.command.CommandHandler.checkClientAuthorization;
import static cloudstorage.command.CommandHandler.checkInvalidArguments;

public class MoveHandler implements CommandHandler {
    private final AttributeKey<String> userKey = AttributeKey.valueOf("user");
    private static final Logger logger = LoggerFactory.getLogger(MoveHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) throws IOException {
        if (checkInvalidArguments(ctx, arguments, Command.MOVE, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        StorageService storageService = channel.attr(STORAGE_KEY).get();
        String login = channel.attr(userKey).get();
        String from = arguments[1];
        String to = arguments[2];
        if (checkClientAuthorization(channel, login, Command.MOVE, logger)) {
            storageService.moveFile(login, from, to);
            logger.info("Successfully moved {} to {} for {}", from, to, login);
            channel.writeAndFlush("Moved successfully");
        }
    }
}
