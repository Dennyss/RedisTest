package processing;

import dto.Point;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Denys Kovalenko on 6/20/2014.
 */
public class PolylineEncoder {


    /**
     * This method encodes entire route that consist of {@code List} of points.
     *
     * @param points {@code List} of Points.
     * @return - encoded {@code String} route.
     */
    public String encodeSegment(List<Point> points) {
        Assert.notNull(points, "Points should not be null");

        StringBuilder encodedRoute = new StringBuilder();
        int latitudePrevious = 0;
        int longitudePrevious = 0;

        for (Point point : points) {
            // Multiply Point coordinates by 1e5
            int latitudeCurrent = (int) Math.floor(point.getLatitude() * 1e5);
            int longitudeCurrent = (int) Math.floor(point.getLongitude() * 1e5);

            // Calculate the one piece of route, or diff between previous position and current
            int latitudeDiff = latitudeCurrent - latitudePrevious;
            int longitudeDiff = longitudeCurrent - longitudePrevious;

            // Save current position for the next processing
            latitudePrevious = latitudeCurrent;
            longitudePrevious = longitudeCurrent;

            encodeCoordinate(encodedRoute, latitudeDiff);
            encodeCoordinate(encodedRoute, longitudeDiff);
        }

        return encodedRoute.toString();
    }


    public String encodeSinglePoint(Point point) {
        Assert.notNull(point, "Point should not be null");

        // Multiply by 1e5
        int latitude = (int) Math.ceil(point.getLatitude() * 1e5);
        int longitude = (int) Math.ceil(point.getLongitude() * 1e5);

        StringBuilder encodedRoute = new StringBuilder();

        encodeCoordinate(encodedRoute, latitude);
        encodeCoordinate(encodedRoute, longitude);

        return encodedRoute.toString();
    }


    private void encodeCoordinate(StringBuilder encodedRoute, int coordinate) {
        int sgn_num = coordinate << 1;
        if (coordinate < 0) {
            sgn_num = ~(sgn_num);
        }

        while (sgn_num >= 0x20) {
            int nextValue = (0x20 | (sgn_num & 0x1f)) + 63;
            encodedRoute.append((char) (nextValue));
            sgn_num >>= 5;
        }
        sgn_num += 63;
        encodedRoute.append((char) (sgn_num));
    }

}
