package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.ArgumentsChecker.checkInvalidArguments;

public class LoginHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoginHandler.class);
    private final AuthenticationService authService;
    private String clientLogin = "unauthorized";
    private int loginAttempts = 3;

    public LoginHandler(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) {
        if (checkInvalidArguments(ctx, arguments, Command.LOGIN)) {
            return;
        }
        Channel channel = ctx.channel();
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
