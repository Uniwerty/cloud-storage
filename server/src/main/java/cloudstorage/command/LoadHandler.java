package cloudstorage.command;

import common.channel.ChannelManager;
import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;
import static cloudstorage.command.CommandHandler.checkUnauthorizedClient;

public class LoadHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoadHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientCommand command) throws IOException, InterruptedException {
        if (checkInvalidArguments(ctx, command, Command.LOAD, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.LOAD, logger)) {
            return;
        }
        String path = command.arguments()[0];
        ChannelManager channelManager = channel.attr(MANAGER_KEY).get();
        channelManager.setFileDownloadHandlers(channel);
        ChannelPromise onWritePromise = channel.newPromise();
        channel.writeAndFlush(
                channel.attr(STORAGE_KEY).get().downloadFile(login, path),
                onWritePromise
        );
        onWritePromise.sync();
        channelManager.setStandardHandlers(channel);
        logger.info("Sent {} to {} successfully", path, login);
    }
}
