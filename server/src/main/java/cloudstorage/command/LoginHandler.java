package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.ArgumentsChecker.checkInvalidArguments;

public class LoginHandler implements CommandHandler {
    private static final AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private String clientLogin = "unauthorized";
    private int loginAttempts = 3;

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) {
        if (checkInvalidArguments(ctx, arguments, Command.LOGIN)) {
            return;
        }
        Channel channel = ctx.channel();
        AuthenticationService authService = channel.attr(AUTH_KEY).get();
        String login = arguments[1];
        String password = arguments[2];
        if (authService.isUserAuthorized(login)) {
            channel.writeAndFlush("The specified user is already logged in.");
            return;
        }
        if (!authService.isUserRegistered(login)) {
            channel.writeAndFlush("Invalid login. Try again.");
            return;
        }
        if (!authService.identifiersMatch(login, password)) {
            loginAttempts--;
            if (loginAttempts == 0) {
                channel.writeAndFlush("You have no more attempts to log in.");
                logger.info(
                        "Client {} was forcibly disconnected because it had no more attempts to log in",
                        login
                );
                ctx.close();
                return;
            }
            channel.writeAndFlush("Invalid password. Try again.");
            return;
        }
        authService.authorizeUser(login);
        clientLogin = login;
        logger.info("Client {} authorized successfully", login);
        channel.writeAndFlush("Authorized successfully.");
    }

    public String getClientLogin() {
        return clientLogin;
    }
}
