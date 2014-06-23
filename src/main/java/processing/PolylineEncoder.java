package processing;

import dto.Point;

import java.util.List;

/**
 * Created by Denys Kovalenko on 6/20/2014.
 */
public class PolylineEncoder {


    /**
     * This method encodes entire route that consist of {@code List} of points.
     * @param points the rout: {@code List} of points.
     * @return - encoded {@code String} route.
     */
    public String encodeRoute(List<Point> points){
        if(points.isEmpty()){
            throw new IllegalArgumentException("The route should be not empty");
        }

        StringBuilder encodedRoute = new StringBuilder(points.size());

        int latitudePrevious = 0;
        int longitudePrevious = 0;

        for(Point point : points){

            // Multiply Point coordinates by 1e5
            int latitudeCurrent = (int) Math.floor(point.getLatitude() * 1e5);
            int longitudeCurrent = (int) Math.floor(point.getLongitude() * 1e5);

            // Calculate the one piece of route, or diff between previous position and current
            int latitudeDiff = latitudeCurrent - latitudePrevious;
            int longitudeDiff = longitudeCurrent - longitudePrevious;

            // Save current position for the next processing
            latitudePrevious = latitudeCurrent;
            longitudePrevious = longitudeCurrent;

            encodedRoute.append(encodeCoordinate(latitudeDiff));
            encodedRoute.append(encodeCoordinate(longitudeDiff));
        }

        return encodedRoute.toString();
    }

    public String encodeSinglePoint(Point point){
        // Multiply by 1e5
        int latitude = (int) Math.ceil(point.getLatitude() * 1e5);
        int longitude = (int) Math.ceil(point.getLongitude() * 1e5);

        return encodeCoordinate(latitude) + encodeCoordinate(longitude);
    }


    // todo: use StringBuilder from invoking method inside this instead of String
    private String encodeCoordinate(int coordinate) {
        int sgn_num = coordinate << 1;
        if (coordinate < 0) {
            sgn_num = ~(sgn_num);
        }

        StringBuffer encodeString = new StringBuffer();
        while (sgn_num >= 0x20) {
            int nextValue = (0x20 | (sgn_num & 0x1f)) + 63;
            encodeString.append((char) (nextValue));
            sgn_num >>= 5;
        }
        sgn_num += 63;
        encodeString.append((char) (sgn_num));

        return encodeString.toString();
    }

}
