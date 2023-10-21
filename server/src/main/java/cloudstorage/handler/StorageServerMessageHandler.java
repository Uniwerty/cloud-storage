package cloudstorage.handler;

import cloudstorage.command.Command;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageServerMessageHandler extends ServerMessageHandler {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final Map<String, String> clientIdentifiers = new ConcurrentHashMap<>();

    /**
     * Handles a message {@code String} read from {@code Channel}.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param msg the message {@code String} to handle
     * @throws Exception if an error occurred during handling
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("Message from {} client: {}", clientLogin, msg);
        String[] arguments = msg.trim().split(WHITESPACE_REGEX);
        if (arguments.length == 0) {
            ctx.channel().writeAndFlush("Empty message received. Try again");
            return;
        }
        try {
            Command command = Enum.valueOf(Command.class, arguments[0].toUpperCase());
            switch (command) {
                case REGISTER -> registerClient(ctx, arguments);
                case LOGIN -> authenticateClient(ctx, arguments);
                case QUIT -> disconnectClient(ctx);
                case HELP -> helpClient(ctx);
                default -> handleUnknownCommand(ctx);
            }
        } catch (IllegalArgumentException e) {
            handleUnknownCommand(ctx);
        }
    }

    /**
     * Registers a new client or reports if it exists already.
     *
     * @param ctx       {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param arguments the command and its arguments
     */
    private void registerClient(ChannelHandlerContext ctx, String[] arguments) {
        if (checkInvalidArguments(ctx, arguments, Command.REGISTER)) {
            return;
        }
        Channel channel = ctx.channel();
        String login = arguments[1];
        String password = arguments[2];
        String repeatedPassword = arguments[3];
        if (clientIdentifiers.containsKey(login)) {
            channel.writeAndFlush("The entered login is already registered. Choose another login.");
            return;
        }
        if (!password.equals(repeatedPassword)) {
            channel.writeAndFlush("The entered passwords mismatch. Try again.");
            return;
        }
        clientIdentifiers.put(login, password);
        logger.info("Client {} registered successfully", login);
        channel.writeAndFlush("Registered successfully.");
    }

    /**
     * Authorizes a client if it specified correct identifiers.
     *
     * @param ctx       {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param arguments the command and its arguments
     */
    private void authenticateClient(ChannelHandlerContext ctx, String[] arguments) {
        if (checkInvalidArguments(ctx, arguments, Command.LOGIN)) {
            return;
        }
        Channel channel = ctx.channel();
        if (loggedIn) {
            channel.writeAndFlush("You are already logged in.");
            return;
        }
        String login = arguments[1];
        String password = arguments[2];
        if (!clientIdentifiers.containsKey(login)) {
            channel.writeAndFlush("Invalid login. Try again.");
            return;
        }
        if (!password.equals(clientIdentifiers.get(login))) {
            loginAttempts--;
            if (loginAttempts == 0) {
                channel.writeAndFlush("You have no more attempts to log in.");
                logger.info(
                        "Client {} was forcibly disconnected because it had no more attempts to log in",
                        clientLogin
                );
                ctx.close();
                return;
            }
            channel.writeAndFlush("Invalid password. Try again.");
            return;
        }
        loggedIn = true;
        clientLogin = login;
        logger.info("Client {} authorized successfully", login);
        channel.writeAndFlush("Authorized successfully.");
    }

    /**
     * Disconnects a client by closing the {@code Channel}.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     */
    private void disconnectClient(ChannelHandlerContext ctx) {
        ctx.close();
    }

    /**
     * Sends to a client list of available commands with their description.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     */
    private void helpClient(ChannelHandlerContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("Available commands:").append(System.lineSeparator());
        for (Command command : Command.values()) {
            sb.append(command.getUsage()).append(System.lineSeparator());
        }
        ctx.channel().writeAndFlush(sb.toString());
    }

    /**
     * Reports a client that the specified command is unknown.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     */
    private void handleUnknownCommand(ChannelHandlerContext ctx) {
        ctx.channel().writeAndFlush("Unknown command given. Use help to see available commands.");
    }

    /**
     * Checks whether the specified {@code command} arguments are correct.
     *
     * @param ctx       {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param arguments the command arguments
     * @param command   the specified {@link Command}
     * @return {@code true} if the {@code arguments} are correct, {@code false} otherwise
     */
    private boolean checkInvalidArguments(ChannelHandlerContext ctx,
                                          String[] arguments,
                                          Command command) {
        if (arguments.length != command.getArgumentsNumber() + 1) {
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
}
