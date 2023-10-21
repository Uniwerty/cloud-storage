package cloudstorage.command;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpHandler implements CommandHandler {
    private static final String COMMANDS_USAGE;
    private static final Logger logger = LoggerFactory.getLogger(HelpHandler.class);

    static {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:").append(System.lineSeparator());
        for (Command command : Command.values()) {
            sb.append(command.getUsage()).append(System.lineSeparator());
        }
        COMMANDS_USAGE = sb.toString();
    }

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) {
        logger.info("Sent commands usage to client");
        ctx.channel().writeAndFlush(COMMANDS_USAGE);
    }
}
