package cloudstorage.command;

import common.command.Command;
import common.message.ClientMessage;
import common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;
import static cloudstorage.command.CommandHandler.checkUnauthorizedClient;

public class MoveHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(MoveHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientMessage command) throws IOException {
        if (checkInvalidArguments(ctx, command, Command.MOVE, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.MOVE, logger)) {
            return;
        }
        String from = command.arguments()[0];
        String to = command.arguments()[1];
        channel.attr(STORAGE_KEY).get().moveFile(login, from, to);
        logger.info("Successfully moved {} to {} for {}", from, to, login);
        channel.writeAndFlush(new ServerMessage(true, command.name(), "Moved successfully"));
    }
}
