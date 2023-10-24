package cloudstorage.handler;

import cloudstorage.command.*;
import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import common.command.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StorageServerMessageHandler extends SimpleChannelInboundHandler<String> {
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");
    private static final AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    private static final Logger logger = LoggerFactory.getLogger(StorageServerMessageHandler.class);
    private static final AuthenticationService authService = new AuthenticationService();
    private static final StorageService storageService = new StorageService();
    private final AttributeKey<String> userKey = AttributeKey.valueOf("user");
    private final UnknownCommandHandler unknownCmdHandler = new UnknownCommandHandler();
    private final Map<Command, CommandHandler> commandHandlers =
            Map.of(
                    Command.REGISTER, new RegisterHandler(),
                    Command.LOGIN, new LoginHandler(),
                    Command.MOVE, new MoveHandler(),
                    Command.HELP, new HelpHandler(),
                    Command.QUIT, new QuitHandler()
            );

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        logger.info("Message from {} client: {}", ctx.channel().attr(userKey).get(), msg);
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
        ctx.channel().attr(userKey).set("unauthorized");
        ctx.channel().attr(AUTH_KEY).set(authService);
        ctx.channel().attr(STORAGE_KEY).set(storageService);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Client {} disconnected", ctx.channel().attr(userKey).get());
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
