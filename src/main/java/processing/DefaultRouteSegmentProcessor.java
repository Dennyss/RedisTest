package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor {
    private static final String ROUT_SEGMENTS_KEY_PREFIX = "routeSegments:";
    private static final String LAST_POINT_TIMESTAMP_KEY_PREFIX = "lastPointTimestamp:";

    @Autowired
    private StringRedisTemplate templateForInput;
    @Autowired
    private RedisTemplate<String, Segment> templateForOutput;
    @Autowired
    private DefaultRedisScript script;


    @Override
    public void applyPoint(String vin, Point point, long timestamp) {
        Assert.notNull(vin, "VIN should not be null");
        Assert.notNull(point, "Point should not be null");

        List<InputMessage> inputMessages = new ArrayList<>(1);
        inputMessages.add(new InputMessage(vin, point, timestamp));

        applyPoints(inputMessages);
    }


    @Override
    public void applyPoints(List<InputMessage> listOfMessages){
        Assert.notNull(listOfMessages, "List of message should not be null");
        Assert.notEmpty(listOfMessages, "List of message should not be empty");

        templateForInput.execute(script, Collections.<String>emptyList(), listOfMessages);
    }


    @Override
    public List<Segment> getSegments(final String vin, final int quantity) {
        Assert.notNull(vin, "VIN should not be null");

        return templateForOutput.opsForList().range(getRouteSegmentsKey(vin), 0, quantity - 1);
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

}
