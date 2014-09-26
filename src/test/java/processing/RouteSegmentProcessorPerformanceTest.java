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
            List<Segment> segments = routeSegmentProcessor.getSegments(vin);
            assertNotNull(segments);
        }

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
                    timestamp += DefaultRouteSegmentProcessor.DEFAULT_TIME_DELIMITER;
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

}