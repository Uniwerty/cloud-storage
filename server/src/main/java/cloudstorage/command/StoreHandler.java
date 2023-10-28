package cloudstorage.command;

import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;
import static cloudstorage.command.CommandHandler.checkUnauthorizedClient;

public class StoreHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientCommand command) {
        Channel channel = ctx.channel();
        if (checkInvalidArguments(ctx, command, Command.STORE, logger)) {
            return;
        }
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.STORE, logger)) {
            return;
        }
        channel.attr(MANAGER_KEY).get().setFileUploadHandlers(channel);
        channel.attr(FILE_KEY).set(command.arguments()[1]);
        logger.info("File storing allowed");
    }
}
