package processing;

import dal.RedisCommandsManager;
import dto.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 1:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class RouteSegmentProcessor {
    public static final int DEFAULT_ROUTE_LENGTH = 20;
    private static final String LATITUDE_KEY = "latitudeCoordinates";
    private static final String LONGITUDE_KEY = "longitudeCoordinates";

    @Autowired
    private PolylineEncoder polylineEncoder;

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    RedisCommandsManager redisCommandsManager;

    public RouteSegmentProcessor(){
    }


    public void savePoint(Point point) throws Exception {
        // todo: Think about ring data structure later. Probably we will not need to save all points, just last 'N' number.
        // Save latitude and longitude into different lists
        redisCommandsManager.lPush(LATITUDE_KEY, String.valueOf(point.getLatitude()));
        redisCommandsManager.lPush(LONGITUDE_KEY, String.valueOf(point.getLongitude()));
    }


    public List<Point> getRoute() throws Exception {
        return getRoute(DEFAULT_ROUTE_LENGTH);
    }


    public List<Point> getRoute(int routeLength) throws Exception {
        List<String> latitudeCoordinates = redisCommandsManager.lRange(LATITUDE_KEY, 0, routeLength -1 );
        List<String> longitudeCoordinates = redisCommandsManager.lRange(LONGITUDE_KEY, 0, routeLength - 1);

        List<Point> route = new ArrayList<>(routeLength);
        for(int i = 0; i < routeLength; i++){
            route.add(new Point(latitudeCoordinates.get(i), longitudeCoordinates.get(i)));
        }

        Collections.reverse(route);
        return route;
    }


    // This two methods use PolylineEncoder.java class encoding logic
//    public String getEncodedRoute() throws Exception {
//        return polylineEncoder.encodeRoute(getRoute(DEFAULT_ROUTE_LENGTH));
//    }


//    public String getEncodedRoute(int routeLength) throws Exception {
//        return polylineEncoder.encodeRoute(getRoute(routeLength));
//    }


    // This method uses encodeRoute.lua script encoding logic
    public String getEncodedRoute(int routeLength) throws Exception {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("processing/encodeRoute.lua"));
        script.setResultType(String.class);
        List<String> keys = new ArrayList<String>();
        keys.add(LATITUDE_KEY);
        keys.add(LONGITUDE_KEY);

        return template.execute(script, keys, String.valueOf(routeLength));
    }

}
