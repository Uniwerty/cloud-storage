package cloudstorage.command;

import io.netty.channel.ChannelHandlerContext;

public interface CommandHandler {
    void handle(ChannelHandlerContext ctx, String[] arguments);
}
