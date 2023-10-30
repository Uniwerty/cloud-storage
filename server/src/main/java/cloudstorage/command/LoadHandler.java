package cloudstorage.command;

import cloudstorage.service.StorageService;
import common.channel.ChannelManager;
import common.command.Command;
import common.message.ClientMessage;
import common.message.ServerMessage;
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
    public void handle(ChannelHandlerContext ctx, ClientMessage command) throws IOException, InterruptedException {
        if (checkInvalidArguments(ctx, command, Command.LOAD, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.LOAD, logger)) {
            return;
        }
        String path = command.arguments()[0];
        StorageService storageService = channel.attr(STORAGE_KEY).get();
        channel.writeAndFlush(
                new ServerMessage(
                        true,
                        Command.LOAD.getName(),
                        "File loading confirmed",
                        storageService.getFileSize(login, path)
                )
        );
        ChannelManager channelManager = channel.attr(MANAGER_KEY).get();
        channelManager.setFileDownloadHandlers(channel);
        ChannelPromise onWritePromise = channel.newPromise();
        channel.writeAndFlush(
                storageService.getChunkedFile(login, path),
                onWritePromise
        );
        onWritePromise.sync();
        channelManager.setStandardHandlers(channel);
        channel.writeAndFlush(new ServerMessage(true, Command.LOAD.getName(), "Sent successfully"));
        logger.info("Sent {} to {} successfully", path, login);
    }
}
