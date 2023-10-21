package cloudstorage.handler;

import cloudstorage.command.*;
import cloudstorage.service.AuthenticationService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StorageServerMessageHandler extends SimpleChannelInboundHandler<String> {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final Logger logger = LoggerFactory.getLogger(StorageServerMessageHandler.class);
    private static final AuthenticationService authService = new AuthenticationService();
    private final LoginHandler loginHandler = new LoginHandler(authService);
    private final UnknownCommandHandler unknownCmdHandler = new UnknownCommandHandler();
    private final Map<Command, CommandHandler> commandHandlers =
            Map.of(
                    Command.REGISTER, new RegisterHandler(authService),
                    Command.LOGIN, loginHandler,
                    Command.HELP, new HelpHandler(),
                    Command.QUIT, new QuitHandler()
            );

    /**
     * Handles a message {@code String} read from {@code Channel}.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param msg the message {@code String} to handle
     * @throws Exception if an error occurred during handling
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("Message from {} client: {}", loginHandler.getClientLogin(), msg);
        String[] arguments = msg.trim().split(WHITESPACE_REGEX);
        if (emptyMessageReceived(ctx, arguments)) {
            return;
        }
        try {
            Command command = Enum.valueOf(Command.class, arguments[0].toUpperCase());
            commandHandlers.getOrDefault(command, unknownCmdHandler).handle(ctx, arguments);
        } catch (IllegalArgumentException e) {
            unknownCmdHandler.handle(ctx, arguments);
        }
    }

    /**
     * Handles a channel register.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @throws Exception if an error occurred during handling
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("New client connected");
    }

    /**
     * Handles a channel closing.
     *
     * @param ctx {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @throws Exception if an error occurred during handling
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client {} disconnected", loginHandler.getClientLogin());
    }

    /**
     * Handles a caught exception.
     *
     * @param ctx   {@link ChannelHandlerContext} of the current {@code ChannelHandler}
     * @param cause the {@link Throwable} caught
     * @throws Exception if an error occurred during handling
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception occurred: {}", cause.getMessage());
        ctx.close();
    }

    private static boolean emptyMessageReceived(ChannelHandlerContext ctx, String[] arguments) {
        if (arguments.length == 0) {
            ctx.channel().writeAndFlush("Empty message received. Try again");
            return true;
        }
        return false;
    }
}
