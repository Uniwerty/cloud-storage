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

    /**
     * If command is correct and client is authorized,
     * notifies client about downloading and sends {@code ChunkedFile}.
     *
     * @param ctx     a {@link ChannelHandlerContext} of the handler
     * @param command the {@link ClientMessage} received
     * @throws IOException          if some exception occurred during IO actions
     * @throws InterruptedException if thread was interrupted
     */
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
        sendChunkedFile(channel, storageService, login, path);
        channel.writeAndFlush(new ServerMessage(true, Command.LOAD.getName(), "Sent successfully"));
        logger.info("Sent {} to {} successfully", path, login);
    }

    private static void sendChunkedFile(Channel channel,
                                        StorageService storageService,
                                        String login,
                                        String path) throws IOException, InterruptedException {
        ChannelManager channelManager = channel.attr(MANAGER_KEY).get();
        channelManager.setFileDownloadHandlers(channel);
        ChannelPromise onWritePromise = channel.newPromise();
        channel.writeAndFlush(
                storageService.getChunkedFile(login, path),
                onWritePromise
        );
        onWritePromise.sync();
        channelManager.setStandardHandlers(channel);
    }
}
