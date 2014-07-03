package processing;

import dal.RedisCommandsManager;
import dto.Point;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RouteSegmentProcessorTest {
    private static final String VIN = "VIN12345";

    @Autowired
    DefaultRouteSegmentProcessor routeSegmentProcessor;

    @Autowired
    RedisCommandsManager redisCommandsManager;


    @Before
    public void cleanDB() throws Exception {
        redisCommandsManager.delete("lastPointTimestamp:" + VIN);
        redisCommandsManager.delete("routeSegments:" + VIN);
    }

    @Test
    public void applyMethodTestWithWrongParams() {
        // First arg is incorrect (null)
        Exception firsAgrIncorrectCase = null;
        try {
            routeSegmentProcessor.applyPoint(null, new Point(23.4, -588.348), 1341434);
        } catch (Exception e) {
            firsAgrIncorrectCase = e;
        }
        assertTrue(firsAgrIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", firsAgrIncorrectCase.getMessage());

        // First arg is incorrect (empty)
        Exception firsAgrIncorrectCase2 = null;
        try {
            routeSegmentProcessor.applyPoint("", new Point(23.4, -588.348), 1341434);
        } catch (Exception e) {
            firsAgrIncorrectCase2 = e;
        }
        assertTrue(firsAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty. ", firsAgrIncorrectCase2.getMessage());

        // Second arg is incorrect
        Exception secondAgrIncorrectCase = null;
        try {
            routeSegmentProcessor.applyPoint("1C4PJMDS3EW135024", null, 234);
        } catch (Exception e) {
            secondAgrIncorrectCase = e;
        }
        assertTrue(secondAgrIncorrectCase instanceof NullPointerException);
        assertEquals("Point cannot be null", secondAgrIncorrectCase.getMessage());

        // Third arg is incorrect
        Exception thirdAgrIncorrectCase = null;
        try {
            routeSegmentProcessor.applyPoint("1C4PJMDS3EW135024", new Point(23.4, -588.348), 0);
        } catch (Exception e) {
            thirdAgrIncorrectCase = e;
        }
        assertTrue(thirdAgrIncorrectCase instanceof IllegalArgumentException);
        assertEquals("Timestamp cannot be negative or zero. ", thirdAgrIncorrectCase.getMessage());

        Exception thirdAgrIncorrectCase2 = null;
        try {
            routeSegmentProcessor.applyPoint("1C4PJMDS3EW135024", new Point(23.4, -588.348), -10);
        } catch (Exception e) {
            thirdAgrIncorrectCase2 = e;
        }
        assertTrue(thirdAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("Timestamp cannot be negative or zero. ", thirdAgrIncorrectCase2.getMessage());

        // All arguments are incorrect
        Exception allAgrsIncorrectCase = null;
        try {
            routeSegmentProcessor.applyPoint(null, null, 0);
        } catch (Exception e) {
            allAgrsIncorrectCase = e;
        }
        assertTrue(allAgrsIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", allAgrsIncorrectCase.getMessage());

        // Different combinations
        Exception diffAgrsIncorrectCase = null;
        try {
            routeSegmentProcessor.applyPoint("1C4PJMDS3EW135024", null, -1);
        } catch (Exception e) {
            diffAgrsIncorrectCase = e;
        }
        assertTrue(diffAgrsIncorrectCase instanceof NullPointerException);
        assertEquals("Point cannot be null", diffAgrsIncorrectCase.getMessage());

        Exception diffAgrsIncorrectCase2 = null;
        try {
            routeSegmentProcessor.applyPoint("", null, 12);
        } catch (Exception e) {
            diffAgrsIncorrectCase2 = e;
        }
        assertTrue(diffAgrsIncorrectCase2 instanceof NullPointerException);
        assertEquals("Point cannot be null", diffAgrsIncorrectCase2.getMessage());
    }


    @Test
    public void applyMethodTestWithNormalParams() throws Exception {
        Point point1 = new Point(38.5, -120.2);
        Point point2 = new Point(40.7, -120.95);
        Point point3 = new Point(43.252, -126.453);
        Point point4 = new Point(44.252, -124.453);

        // Let's start to build new segment
        long currentMillis = System.currentTimeMillis() + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER + 1000;

        routeSegmentProcessor.applyPoint(VIN, point1, currentMillis);
        routeSegmentProcessor.applyPoint(VIN, point2, currentMillis + 1000);
        routeSegmentProcessor.applyPoint(VIN, point3, currentMillis + 2000);

        long stopInterval1 = RouteSegmentProcessor.DEFAULT_TIME_DELIMITER + 1000;

        // Attempt to create new segment
        routeSegmentProcessor.applyPoint(VIN, point4, currentMillis + 2000 + stopInterval1);

        List<String> segments = routeSegmentProcessor.getEncodedSegments(VIN);
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", segments.get(0)); // '0' - is always last segment
    }

    @Test
    public void encodeRoutePerformanceTest() throws Exception {
        // Create 10000 points
        Point[] points = new Point[100];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point((-5000 + i) * 2, (-5000 + i) * 2);
        }

        long currentMillis = System.currentTimeMillis();
        for (int i = 0; i < points.length; i++) {
            routeSegmentProcessor.applyPoint("VIN12346", points[i], currentMillis);
        }

        long processingTime = System.currentTimeMillis() - currentMillis;
        System.out.println("Total time of processing " + points.length + " points is: " + processingTime + " msec.");
        System.out.println("Average processing time of one point is: " + (processingTime / points.length) + " msec.");
    }


}
