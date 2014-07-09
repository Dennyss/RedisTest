package processing;

import common.RedisDao;
import dto.Point;
import dto.Segment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RouteSegmentProcessorTest {
    private static final String VIN = "VIN123";

    private Point point1 = new Point(38.5, -120.2);
    private Point point2 = new Point(40.7, -120.95);
    private Point point3 = new Point(43.252, -126.453);
    private Point point4 = new Point(44.252, -124.453);
    private Point point5 = new Point(-87.0876, -0.934);

    @Autowired
    private DefaultRouteSegmentProcessor routeSegmentProcessor;
    @Autowired
    private RedisDao redisDao;


    @Before
    public void cleanDB() throws Exception {
        redisDao.delete(routeSegmentProcessor.getLastPointTimestampKey(VIN));
        redisDao.delete(routeSegmentProcessor.getRouteSegmentsKey(VIN));
    }

    @Test
    public void shouldCreateOneSegmentWithOnePoint() throws Exception {
        long someTimeWithinRouteSegment = 100;
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);
        Segment segment = segments.get(0);  // '0' - is always last segment

        assertEquals(1, segments.size());
        assertEquals(1, segment.getSegmentPoints().size());
        assertEquals(point1, segment.getSegmentPoints().get(0));
        assertEquals(someTimeWithinRouteSegment, segment.getStartTimestamp());
        assertEquals(someTimeWithinRouteSegment, segment.getEndTimestamp());
    }

    @Test
    public void shouldCreateOneEncodedSegmentWithOnePoint() throws Exception {
        long someTimeWithinRouteSegment = 100;
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);

        List<Segment> encodedSegments = routeSegmentProcessor.getAllEncodedSegments(VIN);
        Segment segment = encodedSegments.get(0);  // '0' - is always last segment

        assertEquals(1, encodedSegments.size());
        assertEquals("_p~iF~ps|U", segment.getEncodedSegment());
    }

    @Test
    public void shouldCreateOneSegmentWithManyPoints() throws Exception {
        long someTimeWithinRouteSegment = 100;
        long someTime = 20;

        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point3, someTimeWithinRouteSegment + someTime);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);
        Segment segment = segments.get(0);  // '0' - is always last segment

        assertEquals(1, segments.size());
        assertEquals(3, segment.getSegmentPoints().size());
        assertEquals(point1, segment.getSegmentPoints().get(0));
        assertEquals(point2, segment.getSegmentPoints().get(1));
        assertEquals(point3, segment.getSegmentPoints().get(2));
        assertEquals(someTimeWithinRouteSegment, segment.getStartTimestamp());
        assertEquals(someTimeWithinRouteSegment + someTime, segment.getEndTimestamp());
    }

    @Test
    public void shouldCreateOneEncodedSegmentWithManyPoints() throws Exception {
        long someTimeWithinRouteSegment = 100;
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point3, someTimeWithinRouteSegment);

        List<Segment> encodedSegments = routeSegmentProcessor.getAllEncodedSegments(VIN);
        Segment segment = encodedSegments.get(0);  // '0' - is always last segment

        assertEquals(1, encodedSegments.size());
        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", segment.getEncodedSegment());
    }

    @Test
    public void shouldCreateManySegmentsWithManyPoints() throws Exception {
        long someTimeWithinRouteSegment = 100;
        long someTime = 20;
        // First segment
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point3, someTimeWithinRouteSegment + someTime);

        long timeAfterLongPause = someTimeWithinRouteSegment + someTime + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;
        // Last segment
        routeSegmentProcessor.applyPoint(VIN, point4, timeAfterLongPause);
        routeSegmentProcessor.applyPoint(VIN, point5, timeAfterLongPause + someTime);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);

        assertEquals(2, segments.size());
        Segment lastSegment = segments.get(0);
        Segment firstSegment = segments.get(1);
        // First segment
        assertEquals(3, firstSegment.getSegmentPoints().size());
        assertEquals(point1, firstSegment.getSegmentPoints().get(0));
        assertEquals(point2, firstSegment.getSegmentPoints().get(1));
        assertEquals(point3, firstSegment.getSegmentPoints().get(2));
        assertEquals(someTimeWithinRouteSegment, firstSegment.getStartTimestamp());
        assertEquals(someTimeWithinRouteSegment + someTime, firstSegment.getEndTimestamp());
        // Last segment
        assertEquals(2, lastSegment.getSegmentPoints().size());
        assertEquals(point4, lastSegment.getSegmentPoints().get(0));
        assertEquals(point5, lastSegment.getSegmentPoints().get(1));
        assertEquals(timeAfterLongPause, lastSegment.getStartTimestamp());
        assertEquals(timeAfterLongPause + someTime, lastSegment.getEndTimestamp());
    }

    @Test
    public void shouldCreateManyEncodedSegmentsWithManyPoints() throws Exception {
        long someTimeWithinRouteSegment = 100;
        long someTime = 20;
        // First segment
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinRouteSegment);
        routeSegmentProcessor.applyPoint(VIN, point3, someTimeWithinRouteSegment + someTime);

        long timeAfterLongPause = someTimeWithinRouteSegment + someTime + RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;
        // Last segment
        routeSegmentProcessor.applyPoint(VIN, point4, timeAfterLongPause);
        routeSegmentProcessor.applyPoint(VIN, point5, timeAfterLongPause + someTime);

        List<Segment> encodedSegments = routeSegmentProcessor.getAllEncodedSegments(VIN);
        assertEquals(2, encodedSegments.size());
        Segment lastSegment = encodedSegments.get(0);
        Segment firstSegment = encodedSegments.get(1);

        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", firstSegment.getEncodedSegment());
        assertEquals("_~amGffrvVngs`Xwx{pV", lastSegment.getEncodedSegment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyMethodWithNullVinInputTest() {
        routeSegmentProcessor.applyPoint(null, point1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyMethodWithNullPointInputTest() {
        routeSegmentProcessor.applyPoint(VIN, null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSegmentsMethodWithNullVinInputTest() {
        routeSegmentProcessor.getSegments(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getEncodedSegmentsMethodWithNullVinInputTest() {
        routeSegmentProcessor.getEncodedSegments(null, 1);
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
            routeSegmentProcessor.applyPoint(VIN, points[i], currentMillis);
        }

        routeSegmentProcessor.getAllEncodedSegments(VIN);

        long processingTime = System.currentTimeMillis() - currentMillis;
        System.out.println("Total time of processing " + points.length + " points is: " + processingTime + " millis.");
        System.out.println("Average processing time of one point is: " + (processingTime / points.length) + " millis.");
    }

}
