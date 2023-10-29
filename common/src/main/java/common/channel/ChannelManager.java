package common.channel;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.json.JsonObjectDecoder;

/**
 * The class for initializing channels and changing handlers dynamically.
 */
public abstract class ChannelManager extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        setStandardHandlers(channel);
    }

    /**
     * Sets handlers to handle standard messages between server and client.
     *
     * @param channel a {@link Channel} to set handlers
     */
    public void setStandardHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        removeAll(pipeline);
        pipeline.addLast("jsonDelimiter", new JsonObjectDecoder())
                .addLast("jsonBytesDecoder", new ByteArrayDecoder())
                .addLast("jsonDecoder", jsonDecoderImpl())
                .addLast("main", mainChannelHandlerImpl())
                .addLast("jsonEncoder", jsonEncoderImpl());
    }

    /**
     * Sets handlers to download file after request received.
     * @param channel a {@link Channel} to set handlers
     */
    public void setFileDownloadHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        removeAll(pipeline);
        pipeline.addLast("fileBytesEncoder", new ByteArrayEncoder());
    }

    /**
     * Sets handlers to upload file after request sent
     * @param channel a {@link Channel} to set handlers
     */
    public void setFileUploadHandlers(Channel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        removeAll(pipeline);
        pipeline.addLast("fileBytesDecoder", new ByteArrayDecoder())
                .addLast("fileUploader", fileUploader())
                .addLast("jsonEncoder", jsonEncoderImpl());
    }

    protected abstract ChannelHandler jsonDecoderImpl();

    protected abstract ChannelHandler mainChannelHandlerImpl();

    protected abstract ChannelHandler jsonEncoderImpl();

    protected abstract ChannelHandler fileUploader();

    private void removeAll(ChannelPipeline pipeline) {
        for (String handler : pipeline.toMap().keySet()) {
            pipeline.remove(handler);
        }
        pipeline.channel().flush();
    }
}

