package cloudstorage.command;

import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;

public class StoreHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientCommand command) {
        Channel channel = ctx.channel();
        channel.attr(MANAGER_KEY).get().setFileUploadHandlers(channel);
        if (checkInvalidArguments(ctx, command, Command.STORE, logger)) {
            return;
        }
        channel.attr(FILE_KEY).set(command.arguments()[1]);
        logger.info("File storing allowed");
    }
}
