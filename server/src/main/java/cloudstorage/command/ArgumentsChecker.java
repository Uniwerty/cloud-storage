package cloudstorage.command;

import io.netty.channel.ChannelHandlerContext;

public class ArgumentsChecker {
    static boolean checkInvalidArguments(ChannelHandlerContext ctx,
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
