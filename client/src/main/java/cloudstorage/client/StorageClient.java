package cloudstorage.client;

import cloudstorage.channel.ClientChannelManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class StorageClient {
    private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
    private final Scanner inScanner = new Scanner(System.in);
    private final String host;
    private final int port;

    public StorageClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ClientChannelManager())
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            Channel channel = future.channel();
            channel.closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            inScanner.close();
        }
    }

    public static void logError(String message) {
        logger.error("Exception occurred: {}", message);
    }
}
