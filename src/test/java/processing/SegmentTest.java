package processing;

import dto.Point;
import dto.Segment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Denys Kovalenko on 7/9/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SegmentTest {
    // Coordinate points are taken from the google example
    private Point point1 = new Point(38.5, -120.2);
    private Point point2 = new Point(40.7, -120.95);
    private Point point3 = new Point(43.252, -126.453);


    @Test
    public void encodeSegmentOnePointTest() {
        List points = new ArrayList(1);
        points.add(point1);
        Segment segment = new Segment(0, 0, points);
        assertEquals("_p~iF~ps|U", segment.getEncodedSegment());
    }

    @Test
    public void encodeSegmentThreePointsTest() {
        // Expected encoded points are taken from the google example
        List<Point> points = new ArrayList<>();
        points.add(point1);
        points.add(point2);
        points.add(point3);

        Segment segment = new Segment(0, 0, points);

        assertEquals("_p~iF~ps|U_ulLnnqC_mqNvxq`@", segment.getEncodedSegment());
    }

    @Test(expected = IllegalArgumentException.class)
    public void encodeSegmentNullInputTest() {
        Segment segment = new Segment(0, 0, null);
        segment.getEncodedSegment();
    }

    @Test
    public void encodeSegmentEmptyInputTest() {
        List<Point> points = new ArrayList<>();
        Segment segment = new Segment(0, 0, points);
        String result = segment.getEncodedSegment();

        assertTrue(result.isEmpty());
    }
}
