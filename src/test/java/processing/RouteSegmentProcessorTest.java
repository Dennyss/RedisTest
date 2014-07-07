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
    private static final String VIN2 = "VIN123456";
    private static final String VIN3 = "VIN1234567";

    // Segment 1
    Point point1 = new Point(38.5, -120.2);
    Point point2 = new Point(40.7, -120.95);
    Point point3 = new Point(43.252, -126.453);

    // Segment 2
    Point point4 = new Point(44.252, -124.453);

    // Segment 3
    Point point5 = new Point(-87.0876, -0.934);
    Point point6 = new Point(-0.058, -34.0984);
    Point point7 = new Point(129.07654, -8.987);
    Point point8 = new Point(-19.725, 125.8740);
    Point point9 = new Point(16.218, 49.513);

    @Autowired
    DefaultRouteSegmentProcessor routeSegmentProcessor;

    @Autowired
    RedisCommandsManager redisCommandsManager;


    @Before
    public void cleanDB() throws Exception {
        redisCommandsManager.delete("lastPointTimestamp:" + VIN);
        redisCommandsManager.delete("routeSegments:" + VIN);
        redisCommandsManager.delete("lastPointTimestamp:" + VIN2);
        redisCommandsManager.delete("routeSegments:" + VIN2);
        redisCommandsManager.delete("lastPointTimestamp:" + VIN3);
        redisCommandsManager.delete("routeSegments:" + VIN3);
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
        assertEquals("VIN cannot be empty", firsAgrIncorrectCase2.getMessage());

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
        assertEquals("Timestamp cannot be negative or zero", thirdAgrIncorrectCase.getMessage());

        Exception thirdAgrIncorrectCase2 = null;
        try {
            routeSegmentProcessor.applyPoint("1C4PJMDS3EW135024", new Point(23.4, -588.348), -10);
        } catch (Exception e) {
            thirdAgrIncorrectCase2 = e;
        }
        assertTrue(thirdAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("Timestamp cannot be negative or zero", thirdAgrIncorrectCase2.getMessage());

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
            routeSegmentProcessor.applyPoint(" ", null, 12);
        } catch (Exception e) {
            diffAgrsIncorrectCase2 = e;
        }
        assertTrue(diffAgrsIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", diffAgrsIncorrectCase2.getMessage());
    }


    @Test
    public void getEncodedMethodWithWrongParamsTest(){
        // First arg is incorrect (null)
        Exception firsAgrIncorrectCase = null;
        try {
            routeSegmentProcessor.getEncodedSegments(null, -1);
        } catch (Exception e) {
            firsAgrIncorrectCase = e;
        }
        assertTrue(firsAgrIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", firsAgrIncorrectCase.getMessage());

        // First arg is incorrect (empty)
        Exception firsAgrIncorrectCase2 = null;
        try {
            routeSegmentProcessor.getEncodedSegments(" ", -1);
        } catch (Exception e) {
            firsAgrIncorrectCase2 = e;
        }
        assertTrue(firsAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", firsAgrIncorrectCase2.getMessage());

        // Second arg is incorrect (0)
        Exception firsAgrIncorrectCase3 = null;
        try {
            routeSegmentProcessor.getEncodedSegments("VIN123", 0);
        } catch (Exception e) {
            firsAgrIncorrectCase3 = e;
        }
        assertTrue(firsAgrIncorrectCase3 instanceof IllegalArgumentException);
        assertEquals("Quantity must be in range between 1 and 20", firsAgrIncorrectCase3.getMessage());

        // Second arg is incorrect (-1)
        Exception firsAgrIncorrectCase4 = null;
        try {
            routeSegmentProcessor.getEncodedSegments("VIN123", -1);
        } catch (Exception e) {
            firsAgrIncorrectCase4 = e;
        }
        assertTrue(firsAgrIncorrectCase4 instanceof IllegalArgumentException);
        assertEquals("Quantity must be in range between 1 and 20", firsAgrIncorrectCase4.getMessage());
    }


    @Test
    public void createOneSegmentTest() throws Exception {
        // Let's start to build new segment
        long currentMillis = System.currentTimeMillis();

        routeSegmentProcessor.applyPoint(VIN, point1, currentMillis);
        routeSegmentProcessor.applyPoint(VIN, point2, currentMillis);
        routeSegmentProcessor.applyPoint(VIN, point3, currentMillis);

        long stopInterval1 = currentMillis + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;

        // Attempt to create new segment
        routeSegmentProcessor.applyPoint(VIN, point4, stopInterval1);

        List<String> segments = routeSegmentProcessor.getEncodedSegments(VIN, 2);
        assertEquals(2, segments.size());   // should be 2 encoded segments
        assertEquals("_~amGffrvV", segments.get(0));  // '0' - is always last segment
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", segments.get(1));
    }


    @Test
    public void createFewSegmentsWithDiffVINsTest() throws Exception {
        // Let's start to build new segment
        long currentMillis = System.currentTimeMillis();

        // Attempt to create segment 1 VIN 1
        routeSegmentProcessor.applyPoint(VIN, point1, currentMillis);
        routeSegmentProcessor.applyPoint(VIN, point2, currentMillis);
        routeSegmentProcessor.applyPoint(VIN, point3, currentMillis);

        // Attempt to create segment 1 VIN 2
        routeSegmentProcessor.applyPoint(VIN2, point1, currentMillis);
        routeSegmentProcessor.applyPoint(VIN2, point2, currentMillis);

        long stopInterval1 = currentMillis + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;

        // Attempt to create segment 2 VIN 1
        routeSegmentProcessor.applyPoint(VIN, point4, stopInterval1);

        // Attempt to create segment 2 VIN 2
        routeSegmentProcessor.applyPoint(VIN2, point3, stopInterval1);
        routeSegmentProcessor.applyPoint(VIN2, point4, stopInterval1);
        routeSegmentProcessor.applyPoint(VIN2, point5, stopInterval1);
        routeSegmentProcessor.applyPoint(VIN2, point6, stopInterval1);

        long stopInterval2 = stopInterval1 + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER + 1000;

        // Attempt to create segment 3 VIN 1
        routeSegmentProcessor.applyPoint(VIN, point5, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN, point6, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN, point7, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN, point8, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN, point9, stopInterval2);

        // Attempt to create segment 3 VIN 2
        routeSegmentProcessor.applyPoint(VIN2, point7, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN2, point8, stopInterval2);
        routeSegmentProcessor.applyPoint(VIN2, point9, stopInterval2);

        // Check VIN 1 related segments
        List<String> segments = routeSegmentProcessor.getEncodedSegments(VIN);
        assertEquals(3, segments.size());   // should be 3 encoded segments
        assertEquals("nhprOnluD_~drOnlliE{qdsWgqgxCtxek[g`cvXybkzEfgaqM", segments.get(0));  // '0' - is always last segment
        assertEquals("_~amGffrvV", segments.get(1));
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", segments.get(2));

        // Check VIN 2 related segments
        List<String> segments2 = routeSegmentProcessor.getEncodedSegments(VIN2);
        assertEquals(3, segments2.size());   // should be 3 encoded segments
        assertEquals("kgyrWvgzu@txek[g`cvXybkzEfgaqM", segments2.get(0));  // '0' - is always last segment
        assertEquals("_t~fGfzxbW_ibE_seKngs`Xwx{pV_~drOnlliE", segments2.get(1));
        assertEquals("_p~iF~ps|U_ulLnnqC", segments2.get(2));
    }


    //@Test
    public void performanceTest() throws Exception {
        // Create 100 points
        Point[] points = new Point[100];
        for (int i = 0; i < points.length; i++) {
            points[i] = new Point((-5000 + i) * 2, (-5000 + i) * 2);
        }

        long currentMillis = System.currentTimeMillis();
        for (int i = 0; i < points.length; i++) {
            routeSegmentProcessor.applyPoint(VIN3, points[i], currentMillis);
        }

        List<String> result = routeSegmentProcessor.getEncodedSegments(VIN3);

        long processingTime = System.currentTimeMillis() - currentMillis;
        assertEquals(1, result.size());
        System.out.println("Total time of processing " + points.length + " points is: " + processingTime + " millis.");
        System.out.println("Average processing time of one point is: " + (processingTime / points.length) + " millis.");
    }

}
