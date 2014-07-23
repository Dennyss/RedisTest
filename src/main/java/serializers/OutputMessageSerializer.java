package serializers;

import dto.Segment;
import org.msgpack.MessagePack;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import templates.SegmentTemplate;

import java.io.IOException;

/**
 * Created by Denys Kovalenko on 7/10/2014.
 */
public class OutputMessageSerializer implements RedisSerializer<Segment> {
    private MessagePack messagePack = new MessagePack();

    @Override
    public byte[] serialize(Segment segments) throws SerializationException {
        return new byte[0];
    }

    @Override
    public Segment deserialize(byte[] bytes) throws SerializationException {
        try {
            return messagePack.read(bytes, SegmentTemplate.getInstance());
        } catch (IOException e) {
            throw new SerializationException("Unable to deserialize", e);
        }
    }
}
