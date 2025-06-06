package cloudstorage.command;

import common.command.Command;
import common.message.ClientMessage;
import common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;
import static cloudstorage.command.CommandHandler.checkUnauthorizedClient;

public class StoreHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreHandler.class);

    /**
     * Prepares for uploading file from client
     *
     * @param ctx     a {@link ChannelHandlerContext} of the handler
     * @param command the received {@link ClientMessage} with store command
     */
    @Override
    public void handle(ChannelHandlerContext ctx, ClientMessage command) {
        Channel channel = ctx.channel();
        if (checkInvalidArguments(ctx, command, Command.STORE, logger)) {
            return;
        }
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.STORE, logger)) {
            return;
        }
        channel.attr(FILE_KEY).set(command.arguments()[1]);
        channel.attr(FILE_SIZE_KEY).set(command.fileSize());
        channel.attr(MANAGER_KEY).get().setFileUploadHandlers(channel);
        channel.writeAndFlush(new ServerMessage(true, command.name(), "Waiting for file loading"));
        logger.info("File storing allowed");
    }
}
