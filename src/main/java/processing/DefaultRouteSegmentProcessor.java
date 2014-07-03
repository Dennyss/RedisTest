package processing;

import common.Validator;
import dal.RedisCommandsManager;
import dto.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor {
    private static final String COORDINATES_KEY_DELIMITER = ":";
    // We will save only last point timestamp for vin. We don't need to know point coordinates.
    public static final String LAST_POINT_TIMESTAMP_KEY = "lastPointTimestamp:";
    public static final String ENCODED_ROUT_SEGMENT_KEY = "encodedRouteSegment:";
    public static final String POINTS_KEY = "points:";

    // Current time delimiter (millis).
    private long timeDelimiter;

    @Autowired
    private RedisCommandsManager redisCommandsManager;
    @Autowired
    private PolylineEncoder polylineEncoder;
    @Autowired
    private Validator validator;



    public DefaultRouteSegmentProcessor(){
        this(DEFAULT_TIME_DELIMITER);
    }

    public DefaultRouteSegmentProcessor(int timeDelimiter){
        this.timeDelimiter = timeDelimiter;
    }


    public void applyPoint(String vin, double latitude, double longitude, long timestamp) throws Exception {
        applyPoint(vin, new Point(latitude, longitude), timestamp);
    }


    @Override
    public void applyPoint(String vin, Point point, long timestamp) throws Exception {
        validator.validateParameters(vin, point, timestamp);

        // Check time diff with prev point
        String prevTimestamp = redisCommandsManager.get(LAST_POINT_TIMESTAMP_KEY + vin);

        // Save current point timestamp vor VIN (override previous)
        redisCommandsManager.set(LAST_POINT_TIMESTAMP_KEY + vin, String.valueOf(timestamp));


        // If time less than, or prev is nil, continue accumulating points
        if(prevTimestamp == null || prevTimestamp.equals("nil") || isSameSegment(prevTimestamp, timestamp) ){
            // Save point to DB, substitute existing with the same coordinates
            redisCommandsManager.rPush(POINTS_KEY + vin, point.getLatitude() + COORDINATES_KEY_DELIMITER + point.getLongitude());
            return;
        }

        // If time more than, create new segment, clean db, save current point as a start point of new segment
        List<String> points = redisCommandsManager.lRange(POINTS_KEY + vin, 0, -1);

        // Calculate route segment and put it to segments DB
        String encodedRouteSegment = polylineEncoder.encodeRoute(points);

        // Save encoded route segment do DB (in the first element of list)
        redisCommandsManager.lPush(ENCODED_ROUT_SEGMENT_KEY + vin, encodedRouteSegment);

        // Remove all prev points (prev segment)
        redisCommandsManager.delete(POINTS_KEY + vin);
        // Save new point as a start of new segment
        redisCommandsManager.rPush(POINTS_KEY + vin, point.getLatitude() + COORDINATES_KEY_DELIMITER + point.getLongitude());
    }


    @Override
    public List<String> getEncodedSegments(String vin) throws Exception {
        // Return from DB all segment by vin
        return redisCommandsManager.lRange(ENCODED_ROUT_SEGMENT_KEY + vin, 0, 19);
    }


    private boolean isSameSegment(String prevTimestampStr, long timestamp) {
        long prevTimestamp = Long.parseLong(prevTimestampStr);
        return (timestamp - prevTimestamp) < timeDelimiter;
    }
}
