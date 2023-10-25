package common.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;

import java.util.Set;

public abstract class ChannelManager extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        setStandardHandlers(channel);
    }

    public void setStandardHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        Set<String> previousHandlers = pipeline.toMap().keySet();
        channel.pipeline()
                .addFirst("jsonEncoder", jsonEncoderImpl())
                .addFirst("main", mainChannelHandlerImpl())
                .addFirst("jsonDecoder", jsonDecoderImpl())
                .addFirst("jsonBytesDecoder", new ByteArrayDecoder())
                .addFirst("jsonDelimiter", new JsonObjectDecoder());
        removeAll(pipeline, previousHandlers);
    }

    public void setFileDownloadHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        Set<String> previousHandlers = pipeline.toMap().keySet();
        channel.pipeline().addFirst("bytesEncoder", new ByteArrayEncoder());
        removeAll(pipeline, previousHandlers);
    }

    public void setFileUploadHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        Set<String> previousHandlers = pipeline.toMap().keySet();
        channel.pipeline()
                .addFirst("fileUploader", fileUploader())
                .addFirst("fileBytesDecoder", new ByteArrayDecoder());
        removeAll(pipeline, previousHandlers);
    }

    protected abstract ChannelHandler jsonDecoderImpl();

    protected abstract ChannelHandler mainChannelHandlerImpl();

    protected abstract ChannelHandler jsonEncoderImpl();

    protected abstract ChannelHandler fileUploader();

    private void removeAll(ChannelPipeline pipeline, Set<String> handlers) {
        for (String handler : handlers) {
            pipeline.remove(handler);
        }
    }
}

