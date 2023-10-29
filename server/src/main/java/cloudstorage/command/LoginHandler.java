package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import common.command.Command;
import common.message.ClientCommand;
import common.message.ServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;

public class LoginHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private int loginAttempts = 3;

    @Override
    public void handle(ChannelHandlerContext ctx, ClientCommand command) {
        if (checkInvalidArguments(ctx, command, Command.LOGIN, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        AuthenticationService authService = channel.attr(AUTH_KEY).get();
        String login = command.arguments()[0];
        String password = command.arguments()[1];
        if (authService.isUserAuthorized(login)) {
            channel.writeAndFlush(
                    new ServerResponse(
                            false,
                            command.name(),
                            "The specified user is already logged in."
                    )
            );
            return;
        }
        if (!authService.isUserRegistered(login)) {
            channel.writeAndFlush(
                    new ServerResponse(
                            false,
                            command.name(),
                            "Invalid login. Try again."
                    )
            );
            return;
        }
        if (!authService.identifiersMatch(login, password)) {
            loginAttempts--;
            if (loginAttempts == 0) {
                channel.writeAndFlush(
                        new ServerResponse(false, command.name(), "You have no more attempts to log in.")
                );
                logger.info(
                        "Client {} was forcibly disconnected because it had no more attempts to log in",
                        login
                );
                ctx.close();
                return;
            }
            channel.writeAndFlush(new ServerResponse(false, command.name(), "Invalid password. Try again."));
            return;
        }
        authService.authorizeUser(login);
        channel.attr(USER_KEY).set(login);
        logger.info("Client {} authorized successfully", login);
        channel.writeAndFlush(new ServerResponse(true, command.name(), "Authorized successfully."));
    }
}
