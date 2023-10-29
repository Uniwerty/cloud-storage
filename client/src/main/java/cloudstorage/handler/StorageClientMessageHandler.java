package cloudstorage.handler;

import cloudstorage.channel.ClientChannelManager;
import cloudstorage.response.LoadResponseHandler;
import cloudstorage.response.MessageSender;
import cloudstorage.response.ResponseHandler;
import cloudstorage.response.StoreResponseHandler;
import common.channel.ChannelManager;
import common.command.Command;
import common.message.ServerResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class StorageClientMessageHandler extends SimpleChannelInboundHandler<ServerResponse> {
    private static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    private static final AttributeKey<Boolean> NEED_CONFIRM_KEY = AttributeKey.valueOf("needConfirm");
    private static final Logger logger = LoggerFactory.getLogger(StorageClientMessageHandler.class);
    private static final ChannelManager channelManager = new ClientChannelManager();
    private final MessageSender messageSender = new MessageSender();
    private final Map<Command, ResponseHandler> responseHandlers =
            Map.of(
                    Command.STORE, new StoreResponseHandler(),
                    Command.LOAD, new LoadResponseHandler()
            );

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ServerResponse response) throws Exception {
        logger.info("Server response: {}", response.message());
        Channel channel = ctx.channel();
        Command command = Command.valueOf(response.command().toUpperCase());
        if (channel.attr(NEED_CONFIRM_KEY).get() && response.success()) {
            responseHandlers.getOrDefault(command, messageSender).handle(channel);
        } else {
            messageSender.handle(channel);
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("Connected to the server");
        ctx.channel().attr(MANAGER_KEY).set(channelManager);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        messageSender.handle(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.info("Disconnected from the server");
        messageSender.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Exception occurred: {}", cause.getMessage());
        ctx.close();
    }
}
