package processing;

import common.Validator;
import dto.Point;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by Denys Kovalenko on 6/20/2014.
 */
public class PolylineEncoder {
    @Autowired
    private Validator validator;


    /**
     * This method encodes entire route that consist of {@code List} of points.
     * @param coordinates {@code List} of points(coordinates in format: 'latitude:longitude').
     * @return - encoded {@code String} route.
     */
    public String encodeRoute(List<String> coordinates){
        validator.validateRoute(coordinates);

        StringBuilder encodedRoute = new StringBuilder(coordinates.size());

        int latitudePrevious = 0;
        int longitudePrevious = 0;

        for(String coordinate : coordinates){
            String[] coordinatesArr = coordinate.split(":");

            // Multiply Point coordinates by 1e5
            int latitudeCurrent = (int) Math.floor(Double.parseDouble(coordinatesArr[0]) * 1e5);
            int longitudeCurrent = (int) Math.floor(Double.parseDouble(coordinatesArr[1]) * 1e5);

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


    public String encodeSinglePoint(Point point){
        // Multiply by 1e5
        int latitude = (int) Math.ceil(point.getLatitude() * 1e5);
        int longitude = (int) Math.ceil(point.getLongitude() * 1e5);

        StringBuilder encodedRoute = new StringBuilder();

        encodeCoordinate(encodedRoute, latitude);
        encodeCoordinate(encodedRoute, longitude);

        return encodedRoute.toString();
    }


    // todo: use StringBuilder from invoking method inside this instead of String
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
