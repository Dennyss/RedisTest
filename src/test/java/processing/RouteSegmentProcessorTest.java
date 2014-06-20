package processing;

import dal.RedisCommandsManager;
import dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Denys Kovalenko on 6/20/2014.
 */

@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith( SpringJUnit4ClassRunner.class)
public class RouteSegmentProcessorTest {
    // Coordinate points are taken from the google example
    private Point point1 = new Point(38.5, -120.2);
    private Point point2 = new Point(40.7, -120.95);
    private Point point3 = new Point(43.252, -126.453);


    @Autowired
    RedisCommandsManager redisCommandsManager;

    @Autowired
    RouteSegmentProcessor routeSegmentProcessor;


    @Test
    public void testSavingPointsAndRouteCreation() throws Exception {
        routeSegmentProcessor.savePoint(point1);
        routeSegmentProcessor.savePoint(point2);
        routeSegmentProcessor.savePoint(point3);

        List<Point> route = routeSegmentProcessor.getRoute(3);

        assertEquals(point1, route.get(0));
        assertEquals(point2, route.get(1));
        assertEquals(point3, route.get(2));

        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", routeSegmentProcessor.getEncodedRoute(3));

    }

    @Test
    public void testSavingPointsAndEncodedRouteCreation() throws Exception {
        routeSegmentProcessor.savePoint(point1);
        routeSegmentProcessor.savePoint(point2);
        routeSegmentProcessor.savePoint(point3);

        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", routeSegmentProcessor.getEncodedRoute(3));
    }


}
