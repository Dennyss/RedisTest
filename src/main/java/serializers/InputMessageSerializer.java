package serializers;

import dto.InputMessage;
import org.msgpack.MessagePack;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.io.IOException;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/9/2014.
 */
public class InputMessageSerializer implements RedisSerializer<List<InputMessage>> {
    private MessagePack messagePack = new MessagePack();

    @Override
    public byte[] serialize(List<InputMessage> inputMessages) throws SerializationException {
        try {
            return messagePack.write(inputMessages);
        } catch (IOException e) {
            throw new SerializationException("Unable to serialize", e);
        }
    }

    @Override
    public List<InputMessage> deserialize(byte[] bytes) throws SerializationException {
        return null;
    }
}
