package processing;

import common.RedisDao;
import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

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

    @Autowired
    private DefaultRouteSegmentProcessor routeSegmentProcessor;
    @Autowired
    private RedisDao redisDao;


    @Before
    public void cleanDB() throws Exception {
        //redisDao.delete(routeSegmentProcessor.getLastPointTimestampKey(VIN));
        //redisDao.delete(routeSegmentProcessor.getRouteSegmentsKey(VIN));
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
    public void shouldCreateOneSegmentWithManyPointsWithinBreakingInterval() throws Exception {
        long someInitialTimeWithinBreakingInterval = 100;
        long someTimeWithinBreakingInterval = someInitialTimeWithinBreakingInterval + 20;

        routeSegmentProcessor.applyPoint(VIN, point1, someInitialTimeWithinBreakingInterval);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinBreakingInterval);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);
        Segment segment = segments.get(0);  // '0' - is always last segment

        assertEquals(1, segments.size());
        assertEquals(2, segment.getSegmentPoints().size());
        assertEquals(point1, segment.getSegmentPoints().get(0));
        assertEquals(point2, segment.getSegmentPoints().get(1));
        assertEquals(someInitialTimeWithinBreakingInterval, segment.getStartTimestamp());
        assertEquals(someTimeWithinBreakingInterval, segment.getEndTimestamp());
    }

    @Test
    public void shouldBrakeSegmentsAfterPredefinedInterval() throws Exception {
        long someInitialTimeWithinBreakingInterval = 100;
        long someTimeWithinBreakingInterval = someInitialTimeWithinBreakingInterval + 20;
        // First segment
        routeSegmentProcessor.applyPoint(VIN, point1, someInitialTimeWithinBreakingInterval);
        routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinBreakingInterval);

        long timeBeyondBreakingInterval = RouteSegmentProcessor.DEFAULT_TIME_DELIMITER + someTimeWithinBreakingInterval;
        long someTimeBeyondBreakingInterval = timeBeyondBreakingInterval + 20;
        // Last segment
        routeSegmentProcessor.applyPoint(VIN, point3, timeBeyondBreakingInterval);
        routeSegmentProcessor.applyPoint(VIN, point4, someTimeBeyondBreakingInterval);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);

        assertEquals(2, segments.size());
        Segment lastSegment = segments.get(0);
        Segment firstSegment = segments.get(1);
        // First segment
        assertEquals(2, firstSegment.getSegmentPoints().size());
        assertEquals(point1, firstSegment.getSegmentPoints().get(0));
        assertEquals(point2, firstSegment.getSegmentPoints().get(1));
        assertEquals(someInitialTimeWithinBreakingInterval, firstSegment.getStartTimestamp());
        assertEquals(someTimeWithinBreakingInterval, firstSegment.getEndTimestamp());
        // Last segment
        assertEquals(2, lastSegment.getSegmentPoints().size());
        assertEquals(point3, lastSegment.getSegmentPoints().get(0));
        assertEquals(point4, lastSegment.getSegmentPoints().get(1));
        assertEquals(timeBeyondBreakingInterval, lastSegment.getStartTimestamp());
        assertEquals(someTimeBeyondBreakingInterval, lastSegment.getEndTimestamp());
    }

    @Test
    public void shouldKeepOnly20LastSegments() throws Exception {
        long someTimeWithinRouteSegment = 100;
        // Create 1st segment with some point
        routeSegmentProcessor.applyPoint(VIN, point1, someTimeWithinRouteSegment);

        // Create 20 next segments with different point in each segment
        for(int segmentIndex = 0; segmentIndex < 20; segmentIndex ++) {
            // Add time after long pause to create new segment each time
            someTimeWithinRouteSegment += RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;

            routeSegmentProcessor.applyPoint(VIN, point2, someTimeWithinRouteSegment);
        }

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);

        // Assert that we have only 20 segments
        assertEquals(20, segments.size());

        // Assert that all 20 segments contain last added points (Point2)
        for(Segment segment : segments) {
            assertEquals(point2, segment.getSegmentPoints().get(0));
            // Assert that any segment from 20 doesn't contain first point (point1).
            // So, point1 was shifted
            assertFalse(segment.getSegmentPoints().contains(point1));
        }
    }

    @Test
    public void shouldProceedWithOneInputMessage(){
        long someTimeWithinRouteSegment = 100;

        List<InputMessage> inputMessages = new ArrayList<>();
        inputMessages.add(new InputMessage(VIN, point1, someTimeWithinRouteSegment));

        routeSegmentProcessor.applyPoints(inputMessages);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);
        Segment segment = segments.get(0);  // '0' - is always last segment

        assertEquals(1, segments.size());
        assertEquals(1, segment.getSegmentPoints().size());
        assertEquals(point1, segment.getSegmentPoints().get(0));
        assertEquals(someTimeWithinRouteSegment, segment.getStartTimestamp());
        assertEquals(someTimeWithinRouteSegment, segment.getEndTimestamp());
    }

    @Test
    public void shouldProceedWithManyInputMessages(){
        List<InputMessage> inputMessages = new ArrayList<>();

        long someInitialTimeWithinBreakingInterval = 100;
        long someTimeWithinBreakingInterval = someInitialTimeWithinBreakingInterval + 20;

        // First segment
        inputMessages.add(new InputMessage(VIN, point1, someInitialTimeWithinBreakingInterval));
        inputMessages.add(new InputMessage(VIN, point2, someTimeWithinBreakingInterval));

        long timeBeyondBreakingInterval = RouteSegmentProcessor.DEFAULT_TIME_DELIMITER + someTimeWithinBreakingInterval;
        long someTimeBeyondBreakingInterval = timeBeyondBreakingInterval + 20;

        // Last segment
        inputMessages.add(new InputMessage(VIN, point3, timeBeyondBreakingInterval));
        inputMessages.add(new InputMessage(VIN, point4, someTimeBeyondBreakingInterval));

        routeSegmentProcessor.applyPoints(inputMessages);

        List<Segment> segments = routeSegmentProcessor.getAllSegments(VIN);

        assertEquals(2, segments.size());
        Segment lastSegment = segments.get(0);
        Segment firstSegment = segments.get(1);
        // First segment
        assertEquals(2, firstSegment.getSegmentPoints().size());
        assertEquals(point1, firstSegment.getSegmentPoints().get(0));
        assertEquals(point2, firstSegment.getSegmentPoints().get(1));
        assertEquals(someInitialTimeWithinBreakingInterval, firstSegment.getStartTimestamp());
        assertEquals(someTimeWithinBreakingInterval, firstSegment.getEndTimestamp());
        // Last segment
        assertEquals(2, lastSegment.getSegmentPoints().size());
        assertEquals(point3, lastSegment.getSegmentPoints().get(0));
        assertEquals(point4, lastSegment.getSegmentPoints().get(1));
        assertEquals(timeBeyondBreakingInterval, lastSegment.getStartTimestamp());
        assertEquals(someTimeBeyondBreakingInterval, lastSegment.getEndTimestamp());
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
    public void applyMethodsWithNullInputTest() {
        routeSegmentProcessor.applyPoints(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void applyMethodsWithEmptyInputTest() {
        routeSegmentProcessor.applyPoints(new ArrayList<InputMessage>());
    }

    @Test
    public void performanceTest() throws Exception {
        // Create 1000 points
        List<InputMessage> inputMessages = createInputMessages();

        long currentMillis = System.currentTimeMillis();
        routeSegmentProcessor.applyPoints(inputMessages);
        List<Segment> segments = routeSegmentProcessor.getAllSegments(createVin());
        long processingTime = System.currentTimeMillis() - currentMillis;

        assertNotNull(segments);

        System.out.println("Total time of processing " + inputMessages.size() + " points is: " + processingTime + " millis.");
        System.out.println("Average processing time of one point is: " + (processingTime / inputMessages.size()) + " millis.");
    }



    private List<InputMessage> createInputMessages() {
        List<InputMessage> inputMessages = new ArrayList<>();
        long timestamp = 0;

        for(int i = 0; i < 100000; i++){
            Point point = createPoint();
            String vin = createVin();
            if(i % 100 == 0){
                timestamp += RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;
            }
            inputMessages.add(new InputMessage(vin, point, timestamp += 20));
        }

        return inputMessages;
    }

    private String createVin() {
        return "VIN123";

    }

    private Point createPoint() {
        double lattitude = -500000 + Math.random() * 1000000;
        double longitude = -500000 + Math.random() * 1000000;

        return new Point(lattitude, longitude);
    }


}
