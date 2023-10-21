package cloudstorage.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cloudstorage.channel.ServerChannelInitializer;

public class StorageServer {
    private static final Logger logger = LoggerFactory.getLogger(StorageServer.class);
    private final int port;

    public static void main(String[] args) {
        try {
            if (validateCommandLineArguments(args)) {
                new StorageServer(Integer.parseInt(args[0])).run();
            }
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage());
        }
    }

    public StorageServer(final int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ServerChannelInitializer())
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private static boolean validateCommandLineArguments(final String[] args) {
        if (args == null || args.length != 1) {
            logger.error("Invalid arguments. Please enter server port.");
            return false;
        }
        return true;
    }
}
