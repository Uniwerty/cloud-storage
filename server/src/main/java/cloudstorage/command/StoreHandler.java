package cloudstorage.command;

import cloudstorage.handler.FileLoaderHandler;
import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;
import static cloudstorage.command.CommandHandler.checkUnauthorizedClient;

public class StoreHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(StoreHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientCommand command) {
        if (checkInvalidArguments(ctx, command, Command.STORE, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addFirst("bytesDecoder", new ByteArrayDecoder())
                .addAfter("bytesDecoder", "fileLoader", new FileLoaderHandler());
        String login = channel.attr(USER_KEY).get();
        if (checkUnauthorizedClient(channel, login, Command.STORE, logger)) {
            return;
        }
        channel.attr(FILE_KEY).set(command.arguments()[1]);
        logger.info("File storing allowed");
    }
}
