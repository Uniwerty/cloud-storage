package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cloudstorage.command.ArgumentsChecker.checkInvalidArguments;

public class RegisterHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);
    private final AuthenticationService authService;

    public RegisterHandler(AuthenticationService authService) {
        this.authService = authService;
    }

    @Override
    public void handle(ChannelHandlerContext ctx, String[] arguments) {
        if (checkInvalidArguments(ctx, arguments, Command.REGISTER)) {
            return;
        }
        Channel channel = ctx.channel();
        String login = arguments[1];
        String password = arguments[2];
        String repeatedPassword = arguments[3];
        if (authService.isUserRegistered(login)) {
            channel.writeAndFlush("The entered login is already registered. Choose another login.");
            return;
        }
        if (!password.equals(repeatedPassword)) {
            channel.writeAndFlush("The entered passwords mismatch. Try again.");
            return;
        }
        authService.registerUser(login, password);
        logger.info("Client {} registered successfully", login);
        channel.writeAndFlush("Registered successfully.");
    }
}
