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

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("New client connected");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client {} disconnected", loginHandler.getClientLogin());
    }

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
