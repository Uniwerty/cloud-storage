package cloudstorage.client;

import cloudstorage.channel.ClientChannelInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * The {@link Client} implementation to work with cloud storage server
 */
public class StorageClient implements Client {
    private static final String QUIT_COMMAND = "quit";
    private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
    private final Scanner inScanner = new Scanner(System.in);
    private final String host;
    private final int port;

    /**
     * Starts client for user to work with cloud storage server.
     *
     * @param args command line arguments
     */
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

    /**
     * Constructs a new {@code StorageClient} to use.
     *
     * @param host the server host to connect
     * @param port the server port to connect
     */
    public StorageClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Starts the client, connects to the server and lets user send commands.
     *
     * @throws Exception if an error occurred during client's working.
     */
    @Override
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

    /**
     * Sends messages from user to server until {@value QUIT_COMMAND} is sent.
     *
     * @param channel the {@link Channel} to send messages
     */
    private void sendMessages(final Channel channel) {
        while (channel.isOpen()) {
            String message = inScanner.nextLine();
            channel.writeAndFlush(message);
            if (message.equals(QUIT_COMMAND)) {
                break;
            }
        }
    }

    /**
     * Checks whether the specified command line arguments is correct for client's work.
     *
     * @param args arguments to validate
     * @return {@code true} if the {@code args} is correct, {@code false} otherwise
     */
    private static boolean validateCommandLineArguments(final String[] args) {
        if (args == null || args.length != 2) {
            logger.error("Invalid arguments. Please enter server host and port.");
            return false;
        }
        return true;
    }
}
