package cloudstorage.response;

import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;

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

    private void sendMessage(Channel channel) {
        String message = inScanner.nextLine();
        ClientCommand command = createClientCommand(message);
        setConfirmRequirement(channel, command);
        channel.writeAndFlush(command);
    }

    private static void setConfirmRequirement(Channel channel, ClientCommand command) {
        if (checkCommandType(command, Command.STORE)) {
            channel.attr(NEED_CONFIRM_KEY).set(true);
            channel.attr(FILE_KEY).set(command.arguments()[0]);
        } else if (checkCommandType(command, Command.LOAD)) {
            channel.attr(NEED_CONFIRM_KEY).set(true);
            channel.attr(FILE_KEY).set(command.arguments()[1]);
        } else {
            channel.attr(NEED_CONFIRM_KEY).set(false);
        }
    }

    private static ClientCommand createClientCommand(String message) {
        String[] command = message.trim().split(WHITESPACE_REGEX, 2);
        String[] arguments;
        if (command.length == 2) {
            arguments = command[1].split(WHITESPACE_REGEX);
        } else {
            arguments = new String[0];
        }
        return new ClientCommand(command[0], arguments);
    }

    private static boolean checkCommandType(ClientCommand clientCommand, Command command) {
        return command.getArgumentsNumber() == clientCommand.arguments().length
                && command.getName().equals(clientCommand.name());
    }
}
