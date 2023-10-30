package cloudstorage.response;

import common.command.Command;
import common.message.ClientMessage;
import io.netty.channel.Channel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class MessageSender implements ResponseHandler {
    private static final String WHITESPACE_REGEX = "\\s+";
    private final Scanner inScanner = new Scanner(System.in);

    @Override
    public void handle(Channel channel) throws Exception {
        sendMessage(channel);
    }

    public void close() {
        inScanner.close();
    }

    private void sendMessage(Channel channel) throws IOException {
        String message = inScanner.nextLine();
        ClientMessage command = createClientCommand(message);
        setConfirmRequirement(channel, command);
        channel.writeAndFlush(command);
    }

    /**
     * Sets the indicator that server confirmation on command required
     *
     * @param channel the connection {@link Channel}
     * @param command the {@link ClientMessage} which execution needs to be allowed.
     */
    private static void setConfirmRequirement(Channel channel, ClientMessage command) {
        if (checkCommandTypeMatching(command.name(), command.arguments(), Command.STORE)) {
            channel.attr(NEED_CONFIRM_KEY).set(true);
            channel.attr(FILE_KEY).set(command.arguments()[0]);
        } else if (checkCommandTypeMatching(command.name(), command.arguments(), Command.LOAD)) {
            channel.attr(NEED_CONFIRM_KEY).set(true);
            channel.attr(FILE_KEY).set(command.arguments()[1]);
        } else {
            channel.attr(NEED_CONFIRM_KEY).set(false);
        }
    }

    private static ClientMessage createClientCommand(String message) throws IOException {
        String[] command = message.trim().split(WHITESPACE_REGEX, 2);
        String[] arguments;
        if (command.length == 2) {
            arguments = command[1].split(WHITESPACE_REGEX);
        } else {
            arguments = new String[0];
        }
        long fileSize = 0;
        if (checkCommandTypeMatching(command[0], arguments, Command.STORE)) {
            fileSize = Files.size(Path.of(arguments[0]));
        }
        return new ClientMessage(command[0], arguments, fileSize);
    }

    private static boolean checkCommandTypeMatching(String name, String[] arguments, Command command) {
        return command.getArgumentsNumber() == arguments.length
                && command.getName().equals(name);
    }
}
