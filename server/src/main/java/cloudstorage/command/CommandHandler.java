package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import common.command.Command;
import common.message.ClientCommand;
import common.message.ServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;

public interface CommandHandler {
    AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");
    AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    AttributeKey<String> USER_KEY = AttributeKey.valueOf("user");
    AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");

    void handle(ChannelHandlerContext ctx, ClientCommand command) throws Exception;

    static boolean checkInvalidArguments(ChannelHandlerContext ctx,
                                         ClientCommand clientCommand,
                                         Command command,
                                         Logger logger) {
        if (clientCommand.arguments().length != command.getArgumentsNumber()) {
            logger.info("Invalid arguments number for {} given", command.getName());
            ctx.channel().writeAndFlush(
                    new ServerResponse(
                            false,
                            String.format(
                                    "Invalid arguments number. Enter %s %s",
                                    command.getName(),
                                    command.getArguments()
                            )
                    )
            );
            return true;
        }
        return false;
    }

    static boolean checkUnauthorizedClient(Channel channel,
                                           String login,
                                           Command command,
                                           Logger logger) {
        if (!channel.attr(AUTH_KEY).get().isUserAuthorized(login)) {
            logger.info("{} command execution refused: client is not authorized", command.getName());
            channel.writeAndFlush(
                    new ServerResponse(
                            false,
                            String.format(
                                    "Cannot execute %s command from unauthorized client. Please log in",
                                    command.getName()
                            )
                    )
            );
            return true;
        }
        return false;
    }
}
