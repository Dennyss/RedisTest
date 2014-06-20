package processing;

import dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
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


    @Test
    public void encodeSinglePointTest(){
        PolylineEncoder polylineEncoder = new PolylineEncoder();

        // Expected encoded points are taken from the google example
        assertEquals("_p~iF~ps|U", polylineEncoder.encodeSinglePoint(point1));
        assertEquals("aflwFn`faV", polylineEncoder.encodeSinglePoint(point2));
        assertEquals("_t~fGfzxbW", polylineEncoder.encodeSinglePoint(point3));
    }


    @Test
    public void encodeRouteTest(){
        PolylineEncoder polylineEncoder = new PolylineEncoder();
        // Create an empty route
        List<Point> route = new ArrayList<>();

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
        assertEquals("The rout should me not empty", emptyCase.getMessage());

        // Positive usecase
        // Add points to route and encode
        route.add(point1);
        route.add(point2);
        route.add(point3);
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", polylineEncoder.encodeRoute(route));
    }
}
