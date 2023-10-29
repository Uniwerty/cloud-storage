package cloudstorage.handler;

import cloudstorage.channel.ServerChannelManager;
import cloudstorage.command.CommandHandler;
import cloudstorage.command.HelpHandler;
import cloudstorage.command.LoadHandler;
import cloudstorage.command.LoginHandler;
import cloudstorage.command.MoveHandler;
import cloudstorage.command.QuitHandler;
import cloudstorage.command.RegisterHandler;
import cloudstorage.command.StoreHandler;
import cloudstorage.command.UnknownCommandHandler;
import cloudstorage.service.AuthenticationService;
import cloudstorage.service.StorageService;
import common.channel.ChannelManager;
import common.command.Command;
import common.message.ClientCommand;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StorageServerMessageHandler extends SimpleChannelInboundHandler<ClientCommand> {
    private static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    private static final AttributeKey<AuthenticationService> AUTH_KEY = AttributeKey.valueOf("auth");
    private static final AttributeKey<StorageService> STORAGE_KEY = AttributeKey.valueOf("storage");
    private static final AttributeKey<String> USER_KEY = AttributeKey.valueOf("user");
    private static final Logger logger = LoggerFactory.getLogger(StorageServerMessageHandler.class);
    private static final ServerChannelManager channelManager = new ServerChannelManager();
    private static final AuthenticationService authService = new AuthenticationService();
    private static final StorageService storageService = new StorageService();
    private final UnknownCommandHandler unknownCmdHandler = new UnknownCommandHandler();
    private final Map<Command, CommandHandler> commandHandlers =
            Map.of(
                    Command.REGISTER, new RegisterHandler(),
                    Command.LOGIN, new LoginHandler(),
                    Command.STORE, new StoreHandler(),
                    Command.LOAD, new LoadHandler(),
                    Command.MOVE, new MoveHandler(),
                    Command.HELP, new HelpHandler(),
                    Command.QUIT, new QuitHandler()
            );

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ClientCommand msg) throws Exception {
        logger.info(
                "Message from {} client: {} {}",
                ctx.channel().attr(USER_KEY).get(),
                msg.name(),
                msg.arguments()
        );
        try {
            Command command = Command.valueOf(msg.name().toUpperCase());
            commandHandlers.getOrDefault(command, unknownCmdHandler).handle(ctx, msg);
        } catch (IllegalArgumentException e) {
            unknownCmdHandler.handle(ctx, msg);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("New client connected");
        ctx.channel().attr(MANAGER_KEY).set(channelManager);
        ctx.channel().attr(AUTH_KEY).set(authService);
        ctx.channel().attr(STORAGE_KEY).set(storageService);
        ctx.channel().attr(USER_KEY).set("unauthorized");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        logger.info("Client {} disconnected", channel.attr(USER_KEY).get());
        channel.attr(AUTH_KEY).get().logOutUser(channel.attr(USER_KEY).get());
        channel.attr(USER_KEY).set("unauthorized");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception occurred: {}", cause.getMessage());
        ctx.close();
    }
}
