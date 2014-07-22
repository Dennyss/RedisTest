package processing;

import dto.InputMessage;
import dto.Point;
import dto.Segment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertNotNull;

/**
 * Created by Denys Kovalenko on 7/18/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class RouteSegmentProcessorPerformanceTest {
    private static final int VINS_NUMBER = 10000;
    private static final int TOTAL_POINTS_NUMBER = 100000;

    private String[] vins = createVins();

    @Autowired
    private DefaultRouteSegmentProcessor routeSegmentProcessor;

    @Test
    public void performanceTest() throws Exception {
        List<InputMessage> inputMessages = createInputMessages();

        long currentMillis = System.currentTimeMillis();
        routeSegmentProcessor.applyPoints(inputMessages);

        // Read segments for each vin
        for (String vin : vins) {
            List<Segment> segments = routeSegmentProcessor.getAllSegments(vin);
            assertNotNull(segments);
        }

        long processingTime = System.currentTimeMillis() - currentMillis;

        System.out.println("Total time of processing " + inputMessages.size() + " points is: " + processingTime + " millis.");
        System.out.println("Average processing time of one point is: " + (processingTime / inputMessages.size()) + " millis.");
    }

    // This test uses Oleg's createInputMessages method.
    @Test
    public void performanceTest2() throws Exception {
        List<InputMessage> inputMessages = createInputMessages(100000);

        long currentMillis = System.currentTimeMillis();
        routeSegmentProcessor.applyPoints(inputMessages);
        long processingTime = System.currentTimeMillis() - currentMillis;

        System.out.println("Total time of processing " + inputMessages.size() + " points is: " + processingTime + " millis.");
        System.out.println("Average processing time of one point is: " + (processingTime / inputMessages.size()) + " millis.");
    }

    private List<InputMessage> createInputMessages() {
        List<InputMessage> inputMessages = new ArrayList<>();
        long timestamp = 0;

        for (String vin : vins) {
            for (int i = 0; i < TOTAL_POINTS_NUMBER / VINS_NUMBER; i++) {
                Point point = createRandomPoint();
                if (i % 100 == 0) {
                    timestamp += RouteSegmentProcessor.DEFAULT_TIME_DELIMITER;
                }
                inputMessages.add(new InputMessage(vin, point, timestamp += 20));
            }
        }

        return inputMessages;
    }

    private String[] createVins() {
        String[] vins = new String[VINS_NUMBER];
        for (int i = 0; i < vins.length; i++) {
            vins[i] = "VIN_" + i + "_" + (int) (Math.random() * 10000);
        }
        return vins;
    }

    private Point createRandomPoint() {
        // Generate coordinates randomly: from -500000 to 500000
        double latitude = Math.random() * 1000000 - 500000;
        double longitude = Math.random() * 1000000 - 500000;

        return new Point(latitude, longitude);
    }

    private List<InputMessage> createInputMessages( int size ) {
        Random rnd = new Random();
        Map<String, Long> vinToTimestamp = new HashMap<>(1000);
        Point thePoint = new Point(0.2, 0.5);

        return IntStream.iterate(0, i -> i + 1).mapToObj(i -> {
            String theVIN = String.format("TESTVIN%06d", rnd.nextInt(1000));
            long rndInterval = rnd.nextInt(10) < 2 ? 10000 : 10;
            Long nextTimestamp = vinToTimestamp.compute(theVIN, ( key, val ) -> val == null ? 0 : val + rndInterval);
            return new InputMessage(theVIN, thePoint, nextTimestamp);
        }).limit(size).collect(Collectors.toList());
    }

}
