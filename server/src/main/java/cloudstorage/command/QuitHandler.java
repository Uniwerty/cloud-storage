package cloudstorage.command;

import common.message.ClientMessage;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuitHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(QuitHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientMessage command) {
        logger.info("Client reported quitting");
        ctx.close();
    }
}
