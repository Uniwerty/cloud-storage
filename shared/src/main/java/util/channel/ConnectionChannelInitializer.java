package util.channel;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.LineEncoder;
import io.netty.handler.codec.string.LineSeparator;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class ConnectionChannelInitializer extends ChannelInitializer<SocketChannel> {
    private static final int MAX_FRAME_LENGTH = 1024;
    private static final Charset MESSAGE_CHARSET = StandardCharsets.UTF_8;
    private static final ByteBuf[] INBOUND_LINE_DELIMITER = Delimiters.nulDelimiter();
    private static final LineSeparator OUTBOUND_LINE_SEPARATOR = new LineSeparator("\0");

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                .addLast(
                        new DelimiterBasedFrameDecoder(MAX_FRAME_LENGTH, INBOUND_LINE_DELIMITER),
                        new StringDecoder(MESSAGE_CHARSET),
                        mainChannelHandlerImpl(),
                        new LineEncoder(OUTBOUND_LINE_SEPARATOR, MESSAGE_CHARSET)
                );
    }

    protected abstract ChannelHandler mainChannelHandlerImpl();
}

