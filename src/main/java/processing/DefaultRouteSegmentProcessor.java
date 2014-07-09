package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.msgpack.MessagePack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor {
    private static final String ROUT_SEGMENTS_KEY_PREFIX = "routeSegments:";
    private static final String LAST_POINT_TIMESTAMP_KEY_PREFIX = "lastPointTimestamp:";
    private MessagePack messagePack = new MessagePack();

    @Autowired
    private StringRedisTemplate template;
    @Autowired
    private DefaultRedisScript script;


    @Override
    public void applyPoint(String vin, Point point, long timestamp) {
        Assert.notNull(vin, "VIN should not be null");
        Assert.notNull(point, "Point should not be null");

        InputMessage inputMessage = new InputMessage(vin, point, timestamp);
        template.execute(script, Collections.<String>emptyList(), inputMessage);
    }


    @Override
    public List<Segment> getSegments(final String vin, final int quantity) {
        Assert.notNull(vin, "VIN should not be null");

        return template.execute(new RedisCallback<List<Segment>>() {
            @Override
            public List<Segment> doInRedis(RedisConnection connection) throws DataAccessException {
                return unpackAll(connection.lRange(getRouteSegmentsKey(vin).getBytes(), 0, quantity - 1));
            }
        });
    }


    @Override
    public List<Segment> getAllSegments(String vin) {
        return getSegments(vin, 0);
    }


    public String getRouteSegmentsKey(String vin) {
        return ROUT_SEGMENTS_KEY_PREFIX + vin;
    }


    public String getLastPointTimestampKey(String vin) {
        return LAST_POINT_TIMESTAMP_KEY_PREFIX + vin;
    }


    private List<Segment> unpackAll(List<byte[]> packedSegments) {
        List<Segment> unpackedSegments = new ArrayList(packedSegments.size());
        for (byte[] packedSegment : packedSegments) {
            unpackedSegments.add(unpack(packedSegment));
        }
        return unpackedSegments;
    }


    private Segment unpack(byte[] packedSegment) {
        try {
            return messagePack.read(packedSegment, Segment.class);
        } catch (IOException e) {
            throw new SerializationException("Unable to deserialize", e);
        }
    }

}
