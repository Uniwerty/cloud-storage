package cloudstorage.client;

import cloudstorage.channel.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class StorageClient {
    private static final String QUIT_COMMAND = "quit";
    private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
    private final Scanner inScanner = new Scanner(System.in);
    private final String host;
    private final int port;

    public static void main(String[] args) {
        try {
            if (validateCommandLineArguments(args)) {
                String host = args[0];
                int port = Integer.parseInt(args[1]);
                new StorageClient(host, port).start();
            }
        } catch (Exception e) {
            logger.error("Exception occurred: {}", e.getMessage());
        }
    }

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
                    .handler(new ClientChannelInitializer())
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            sendMessages(future.channel());
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            inScanner.close();
        }
    }

    private void sendMessages(Channel channel) {
        while (channel.isOpen()) {
            String message = inScanner.nextLine();
            channel.writeAndFlush(message);
            if (message.equals(QUIT_COMMAND)) {
                break;
            }
        }
    }

    private static boolean validateCommandLineArguments(String[] args) {
        if (args == null || args.length != 2) {
            logger.error("Invalid arguments. Please enter server host and port.");
            return false;
        }
        return true;
    }
}
