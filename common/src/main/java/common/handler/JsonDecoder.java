package common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class JsonDecoder<T> extends MessageToMessageDecoder<byte[]> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Class<T> objectClass;

    public JsonDecoder(Class<T> objectClass) {
        this.objectClass = objectClass;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, byte[] msg, List<Object> out) throws Exception {
        out.add(objectMapper.readValue(msg, objectClass));
    }
}
