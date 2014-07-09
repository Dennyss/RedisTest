package processing;

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
     * Returns <quantity/> built segments
     * @param vin
     * @param quantity
     * @return  List of Segment
     */
    public List<Segment> getSegments(String vin, int quantity);

    /**
     * Returns all built segments
     * @param vin
     * @return
     */
    public List<Segment> getAllSegments(String vin);

}
