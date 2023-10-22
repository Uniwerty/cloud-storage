package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;

public interface CommandHandler {
    AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");

    void handle(ChannelHandlerContext ctx, String[] arguments) throws Exception;

    static boolean checkInvalidArguments(ChannelHandlerContext ctx,
                                         String[] arguments,
                                         Command command,
                                         Logger logger) {
        if (arguments.length != command.getArgumentsNumber() + 1) {
            logger.info("Invalid arguments number for {} given", command.getName());
            ctx.channel().writeAndFlush(
                    String.format(
                            "Invalid arguments number. Enter %s %s",
                            command.getName(),
                            command.getArguments()
                    )
            );
            return true;
        }
        return false;
    }

    static boolean checkClientAuthorization(Channel channel,
                                            String login,
                                            Command command,
                                            Logger logger) {
        if (!channel.attr(AUTH_KEY).get().isUserAuthorized(login)) {
            logger.info("{} command execution refused: client is not authorized", command.getName());
            channel.writeAndFlush(
                    String.format(
                            "Cannot execute %s command from unauthorized client. Please log in",
                            command.getName()
                    )
            );
            return false;
        }
        return true;
    }
}
