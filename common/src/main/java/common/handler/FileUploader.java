package common.handler;

import common.channel.ChannelManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;

import java.io.IOException;

public abstract class FileUploader extends SimpleChannelInboundHandler<ByteBuf> {
    protected static final AttributeKey<ChannelManager> MANAGER_KEY = AttributeKey.valueOf("manager");
    protected static final AttributeKey<String> FILE_KEY = AttributeKey.valueOf("file");
    protected static final AttributeKey<Long> FILE_SIZE_KEY = AttributeKey.valueOf("fileSize");
    protected long bytesRead = 0;

    /**
     * Reads file chunks and writes them
     * until read bytes number gets greater or equal to file size.
     *
     * @param ctx       a {@link ChannelHandlerContext} of the handler
     * @param fileChunk the file chunk
     * @throws Exception if it occurred while uploading
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf fileChunk) throws Exception {
        Channel channel = ctx.channel();
        uploadFileChunk(channel, fileChunk);
        if (bytesRead == channel.attr(FILE_SIZE_KEY).get()) {
            completeUpload(channel);
        }
    }

    protected abstract void uploadFileChunk(Channel channel, ByteBuf fileChunk) throws IOException;

    protected abstract void completeUpload(Channel channel);
}
