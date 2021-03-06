package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public interface RouteSegmentProcessor {
    // Default delimiter time, that delimits route segments from each other (millis).
    public static final int DEFAULT_TIME_DELIMITER = 120000;  // 120000 millis= 2 min

    /**
     * This method is input interface that applies point of coordinates with vin and timestamp,
     * builds route segments and save it into the database.
     * @param vin - vin
     * @param point - Coordinate point (latitude and longitude)
     * @param timestamp - actual time information of point.
     */
    public void applyPoint(String vin, Point point, long timestamp);

    /**
     * This method is input interface that applies batch of messages (VINs with points and timestamps),
     * builds route segments and save it into the database.
     * @param listOfMessages - batch of messages
     */
    public void applyPoints(List<InputMessage> listOfMessages);

    /**
     * Returns <quantity/> built segments
     * @param vin
     * @return  List of Segment
     */
    public List<Segment> getSegments(String vin);

}
