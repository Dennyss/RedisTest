package processing;

import dto.Point;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public interface RouteSegmentProcessor {
    // Default delimiter time, that delimits route segments from each other (millis).
    public static final int DEFAULT_TIME_DELIMITER = 120000;  // 120000 millis= 2 min

    /**
     * This method is input interface that applies point of coordinates with vin and timestamp
     * and perform logic operations to calculate rout segment encoding and save in to database.
     * @param vin - vin
     * @param point - Coordinate point (latitude and longitude)
     * @param timestamp - actual time information of point.
     */
    public void applyPoint(String vin, Point point, long timestamp) throws Exception;

    /**
     * This method returns max 20 last encoded route segments for particular VIN.
     * @param vin - VIN for by which encoded route segments will be fetched.
     * @return List of String (encoded route segments).
     */
    public List<String> getEncodedSegments(String vin) throws Exception;
}
