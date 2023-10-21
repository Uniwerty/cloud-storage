package cloudstorage.command;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UnknownCommandHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(UnknownCommandHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) {
        logger.info("Received unknown command");
        ctx.channel().writeAndFlush("Unknown command given. Use help to see available commands.");
    }
}
