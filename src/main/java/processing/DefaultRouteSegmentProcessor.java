package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor {
    // Packed route segment data structure
    private static final String PACKED_ROUT_SEGMENTS_KEY_PREFIX = "packedRouteSegments:";
    // Unpacked route segment data structure
    private static final String LAST_POINT_TIMESTAMP_KEY_PREFIX = "lastPointTimestamp:";
    private static final String ROUTE_SEGMENT_START_TIMESTAMP_KEY_PREFIX = "routeSegmentStartTimestamp:";
    private static final String ROUTE_SEGMENT_END_TIMESTAMP_KEY_PREFIX = "routeSegmentEndTimestamp:";
    private static final String ROUT_SEGMENT_POINTS_KEY_PREFIX = "routeSegmentPoints:";

    private RedisTemplate<String, List<InputMessage>> templateForInput;
    private RedisTemplate<String, Segment>            templateForOutput;
    private RedisScript processSegmentsScript;
    private RedisScript<List<Segment>> retrieveSegmentsScript;

    public void setTemplateForInput( RedisTemplate<String, List<InputMessage>> templateForInput ) {
        this.templateForInput = templateForInput;
    }

    public void setTemplateForOutput( RedisTemplate<String, Segment> templateForOutput ) {
        this.templateForOutput = templateForOutput;
    }

    public void setProcessSegmentsScript( RedisScript processSegmentsScript ) {
        this.processSegmentsScript = processSegmentsScript;
    }

    public void setRetrieveSegmentsScript( RedisScript retrieveSegmentsScript ) {
        this.retrieveSegmentsScript = retrieveSegmentsScript;
    }

    @Override
    public void applyPoint( String vin, Point point, long timestamp ) {
        Assert.notNull(vin, "VIN should not be null");
        Assert.notNull(point, "Point should not be null");

        List<InputMessage> inputMessages = new ArrayList<>(1);
        inputMessages.add(new InputMessage(vin, point, timestamp));

        applyPoints(inputMessages);
    }


    @Override
    public void applyPoints( List<InputMessage> listOfMessages ) {
        Assert.notNull(listOfMessages, "List of message should not be null");
        Assert.notEmpty(listOfMessages, "List of message should not be empty");

        templateForInput.execute(processSegmentsScript, Collections.<String>emptyList(), listOfMessages);
    }


    @Override
    public List<Segment> getSegments( final String vin, final int quantity ) {
        Assert.notNull(vin, "VIN should not be null");

        List<String> keys = new ArrayList();
        keys.add(vin);
        // Pack all unpacked segments
        templateForOutput.execute(retrieveSegmentsScript, keys);

        return templateForOutput.opsForList().range(getPackedRouteSegmentsKey(vin), 0, quantity - 1);
    }


    @Override
    public List<Segment> getAllSegments( String vin ) {
        return getSegments(vin, 0);
    }


    public String getPackedRouteSegmentsKey( String vin ) {
        return PACKED_ROUT_SEGMENTS_KEY_PREFIX + vin;
    }

    public String getLastPointTimestampKey( String vin ) {
        return LAST_POINT_TIMESTAMP_KEY_PREFIX + vin;
    }

    public String getRouteSegmentStartTimestampKey( String vin ) {
        return ROUTE_SEGMENT_START_TIMESTAMP_KEY_PREFIX + vin;
    }

    public String getRouteSegmentEndTimestampKey( String vin ) {
        return ROUTE_SEGMENT_END_TIMESTAMP_KEY_PREFIX + vin;
    }

    public String getRouteSegmentPointsKey( String vin ) {
        return ROUT_SEGMENT_POINTS_KEY_PREFIX + vin;
    }

}
