package common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class JsonEncoder<T> extends MessageToByteEncoder<T> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonEncoder(Class<? extends T> outboundMessageType) {
        super(outboundMessageType);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, T msg, ByteBuf out) throws Exception {
        out.writeBytes(objectMapper.writeValueAsBytes(msg));
    }


}
