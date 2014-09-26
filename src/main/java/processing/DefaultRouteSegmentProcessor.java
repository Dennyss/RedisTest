package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.Assert;
import serializers.InputMessageSerializer;
import serializers.OutputMessageSerializer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor, InitializingBean {
    // Default delimiter time, that delimits route segments from each other (millis).
    public static final int DEFAULT_TIME_DELIMITER = 120000;  // 120000 millis= 2 min
    // Packed route segment data structure
    private static final String PACKED_SEGMENTS_KEY_PREFIX = "packedSegments:";
    // Unpacked route segment data structure
    private static final String LAST_POINT_TIMESTAMP_KEY_PREFIX = "lastPointTimestamp:";
    private static final String SEGMENT_TIMESTAMPS_KEY_PREFIX = "segmentTimestamps:";
    private static final String SEGMENT_POINTS_LAT_KEY_PREFIX = "segmentPointsLat:";
    private static final String SEGMENT_POINTS_LON_KEY_PREFIX = "segmentPointsLon:";

    private RedisTemplate<String, List<InputMessage>> templateForInput;
    private RedisTemplate<String, List<Segment>> templateForOutput;
    private RedisScript processSegmentsScript;
    private RedisScript<List<Segment>> retrieveSegmentsScript;

    private JedisConnectionFactory jedisConnectionFactory;

    public DefaultRouteSegmentProcessor(JedisConnectionFactory jedisConnectionFactory) {
        this.jedisConnectionFactory = jedisConnectionFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        templateForInput = createTemplateForInput();
        templateForOutput = createTemplateForOutput();
        processSegmentsScript = createProcessSegmentsScript();
        retrieveSegmentsScript = createRetrieveSegmentsScript();
    }


    public void applyPoint( String vin, Point point, long timestamp ) {
        Assert.notNull(vin, "VIN should not be null");
        Assert.notNull(point, "Point should not be null");

        List<InputMessage> inputMessages = new ArrayList<>(1);
        inputMessages.add(new InputMessage(vin, point, timestamp));

        applyPoints(inputMessages);
    }


    public void applyPoints( List<InputMessage> listOfMessages ) {
        Assert.notNull(listOfMessages, "List of message should not be null");
        Assert.notEmpty(listOfMessages, "List of message should not be empty");

        templateForInput.execute(processSegmentsScript, Collections.<String>emptyList(), listOfMessages);
    }


    public List<Segment> getSegments( String vin ) {
        Assert.notNull(vin, "VIN should not be null");

        List<String> keys = new ArrayList();
        keys.add(vin);
        return templateForOutput.execute(retrieveSegmentsScript, keys);
    }


    public String getPackedSegmentsKey(String vin) {
        return PACKED_SEGMENTS_KEY_PREFIX + vin;
    }

    public String getLastPointTimestampKey( String vin ) {
        return LAST_POINT_TIMESTAMP_KEY_PREFIX + vin;
    }

    public String getSegmentTimestampsKey(String vin) {
        return SEGMENT_TIMESTAMPS_KEY_PREFIX + vin;
    }

    public String getSegmentPointsLatitudeKey(String vin) {
        return SEGMENT_POINTS_LAT_KEY_PREFIX + vin;
    }

    public String getSegmentPointsLongitudeKey(String vin) {
        return SEGMENT_POINTS_LON_KEY_PREFIX + vin;
    }

    private RedisTemplate<String, List<InputMessage>> createTemplateForInput() {
        RedisTemplate<String, List<InputMessage>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new InputMessageSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    private RedisTemplate<String, List<Segment>> createTemplateForOutput() {
        RedisTemplate<String, List<Segment>> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new OutputMessageSerializer());
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    private RedisScript createProcessSegmentsScript() {
        DefaultRedisScript script = new DefaultRedisScript();
        script.setLocation(new ClassPathResource("processing/processSegments.lua"));
        return script;
    }

    private RedisScript createRetrieveSegmentsScript() {
        DefaultRedisScript script = new DefaultRedisScript();
        script.setLocation(new ClassPathResource("processing/retrieveSegments.lua"));
        script.setResultType(List.class);

        return script;
    }

}