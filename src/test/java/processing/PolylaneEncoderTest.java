package processing;

import dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Denys Kovalenko on 6/20/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith( SpringJUnit4ClassRunner.class)
public class PolylaneEncoderTest {
    // Coordinate points are taken from the google example
    private Point point1 = new Point(38.5, -120.2);
    private Point point2 = new Point(40.7, -120.95);
    private Point point3 = new Point(43.252, -126.453);

    @Autowired
    private PolylineEncoder polylineEncoder;

    @Test
    public void encodeSinglePointTest(){
        // Expected encoded points are taken from the google example
        assertEquals("_p~iF~ps|U", polylineEncoder.encodeSinglePoint(point1));
        assertEquals("aflwFn`faV", polylineEncoder.encodeSinglePoint(point2));
        assertEquals("_t~fGfzxbW", polylineEncoder.encodeSinglePoint(point3));
    }


    @Test
    public void encodeRouteTest(){
        // Create an empty route
        List<String> route = new ArrayList<>();

        // Negative usecases
        // Null case
        Exception nullCase = null;
        try{
            polylineEncoder.encodeRoute(null);
        }catch(Exception e){
            nullCase = e;
        }
        assertTrue(nullCase instanceof NullPointerException);

        // Empty route case
        Exception emptyCase = null;
        try{
            polylineEncoder.encodeRoute(route);
        }catch(Exception e){
            emptyCase = e;
        }
        assertTrue(emptyCase instanceof IllegalArgumentException);
        assertEquals("The route cannot be empty", emptyCase.getMessage());

        // Positive usecase
        // Add points to route and encode
        route.add(point1.getLatitude() + ":" + point1.getLongitude());
        route.add(point2.getLatitude() + ":" + point2.getLongitude());
        route.add(point3.getLatitude() + ":" + point3.getLongitude());
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", polylineEncoder.encodeRoute(route));
    }

    @Test
    public void encodeRoutePerformanceTest() throws Exception {
        // Create 1000 000 points
        int pointsQuantity = 1000000;
        List<String> route = new ArrayList<>(pointsQuantity);
        for(int i = 0; i < pointsQuantity; i++){
            route.add(String.valueOf((-5000 + i) * 2) + ":" + String.valueOf((-5000 + i) * 2));
        }

        long currentMillis = System.currentTimeMillis();
        String result = polylineEncoder.encodeRoute(route);
        long processingTime = System.currentTimeMillis() - currentMillis;

        System.out.println("Total time of processing " + route.size() + " points is: " + processingTime + " msec.");
        System.out.println("Average processing time of one point is: " + (processingTime / route.size()) + " msec.");
        System.out.println("Result: " + result.substring(0, 100));
    }
}
