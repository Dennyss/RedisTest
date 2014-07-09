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
@RunWith(SpringJUnit4ClassRunner.class)
public class PolylaneEncoderTest {
    // Coordinate points are taken from the google example
    private Point point1 = new Point(38.5, -120.2);
    private Point point2 = new Point(40.7, -120.95);
    private Point point3 = new Point(43.252, -126.453);

    @Autowired
    private PolylineEncoder polylineEncoder;

    @Test
    public void encodeSinglePointTest() {
        assertEquals("_p~iF~ps|U", polylineEncoder.encodeSinglePoint(point1));
    }

    @Test
    public void encodeSegmentOnePointTest() {
        List<Point> segment = new ArrayList<>();
        segment.add(point1);

        assertEquals("_p~iF~ps|U", polylineEncoder.encodeSegment(segment));
    }

    @Test
    public void encodeSegmentThreePointsTest() {
        // Expected encoded points are taken from the google example
        List<Point> segment = new ArrayList<>();
        segment.add(point1);
        segment.add(point2);
        segment.add(point3);

        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", polylineEncoder.encodeSegment(segment));
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeSinglePointNullInputTest() {
        polylineEncoder.encodeSinglePoint(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeSegmentNullInputTest() {
        polylineEncoder.encodeSegment(null);
    }

    @Test
    public void encodeSegmentEmptyInputTest() {
        List<Point> points = new ArrayList<>();
        String result = polylineEncoder.encodeSegment(points);

        assertTrue(result.isEmpty());
    }

}
