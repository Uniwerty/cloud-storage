package cloudstorage.command;

import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import common.command.Command;
import common.message.ClientMessage;
import common.message.ServerMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static cloudstorage.command.CommandHandler.checkInvalidArguments;

public class RegisterHandler implements CommandHandler {
    private static final Logger logger = LoggerFactory.getLogger(RegisterHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, ClientMessage command) throws IOException {
        if (checkInvalidArguments(ctx, command, Command.REGISTER, logger)) {
            return;
        }
        Channel channel = ctx.channel();
        AuthenticationService authService = channel.attr(AUTH_KEY).get();
        StorageService storageService = channel.attr(STORAGE_KEY).get();
        String login = command.arguments()[0];
        String password = command.arguments()[1];
        String repeatedPassword = command.arguments()[2];
        if (authService.isUserRegistered(login)) {
            channel.writeAndFlush(
                    new ServerMessage(
                            false,
                            command.name(),
                            "The entered login is already registered. Choose another login."
                    )
            );
            return;
        }
        if (!password.equals(repeatedPassword)) {
            channel.writeAndFlush(
                    new ServerMessage(false, command.name(), "The entered passwords mismatch. Try again.")
            );
            return;
        }
        authService.registerUser(login, password);
        storageService.createUserDirectory(login);
        logger.info("Client {} registered successfully", login);
        channel.writeAndFlush(new ServerMessage(true, command.name(), "Registered successfully."));
    }
}
