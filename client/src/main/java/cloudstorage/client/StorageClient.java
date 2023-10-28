package cloudstorage.client;

import cloudstorage.channel.ClientChannelManager;
import cloudstorage.handler.ClientFileLoader;
import common.channel.ChannelManager;
import common.command.Command;
import common.message.ClientCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class StorageClient {
    private static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    private static final String WHITESPACE_REGEX = "\\s+";
    private static final int MAX_NORMAL_FILE_SIZE = 4096;
    private static final Logger logger = LoggerFactory.getLogger(StorageClient.class);
    private final ChannelManager channelManager = new ClientChannelManager();
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
                    .handler(channelManager)
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.connect(host, port).sync();
            sendMessages(future.channel());
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            inScanner.close();
        }
    }

    public static void logError(String message) {
        logger.error("Exception occurred: {}", message);
    }

    private void sendMessages(Channel channel) throws IOException, InterruptedException {
        while (channel.isOpen()) {
            String message = inScanner.nextLine();
            ClientCommand command = getCommand(message);
            channel.writeAndFlush(command);
            if (checkCommandType(command, Command.STORE)) {
                sendFile(channel, command);
            } else if (checkCommandType(command, Command.QUIT)) {
                break;
            }
        }
    }

    private static ClientCommand getCommand(String message) {
        String[] command = message.trim().split(WHITESPACE_REGEX, 2);
        String[] arguments;
        if (command.length == 2) {
            arguments = command[1].split(WHITESPACE_REGEX);
        } else {
            arguments = new String[0];
        }
        return new ClientCommand(command[0], arguments);
    }

    private static boolean checkCommandType(ClientCommand clientCommand, Command command) {
        return command.getArgumentsNumber() == clientCommand.arguments().length
                && command.getName().equals(clientCommand.name());
    }

    private void sendFile(Channel channel, ClientCommand command) throws IOException, InterruptedException {
        Path filepath = Path.of(command.arguments()[0]);
        if (Files.size(filepath) <= MAX_NORMAL_FILE_SIZE) {
            sendSmallFile(channel, filepath);
        }
    }

    private void sendSmallFile(Channel channel, Path filepath) throws IOException, InterruptedException {
        channelManager.setFileDownloadHandlers(channel);
        ChannelPromise onWritePromise = channel.newPromise();
        channel.writeAndFlush(Files.readAllBytes(filepath), onWritePromise);
        onWritePromise.sync();
        channel.pipeline().remove("byteArrayEncoder");
        channelManager.setStandardHandlers(channel);
    }
    }
}
