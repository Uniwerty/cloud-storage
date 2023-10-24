package common.channel;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.json.JsonObjectDecoder;

public abstract class ConnectionChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        channel.pipeline()
                .addLast("delimiter", new JsonObjectDecoder())
                .addLast("byteArrayDecoder", new ByteArrayDecoder())
                .addLast("decoder", jsonDecoderImpl())
                .addLast("main", mainChannelHandlerImpl())
                .addLast("encoder", jsonEncoderImpl());
    }

    protected abstract ChannelHandler jsonDecoderImpl();

    protected abstract ChannelHandler mainChannelHandlerImpl();

    protected abstract ChannelHandler jsonEncoderImpl();
}

