package templates;

import dto.Point;
import dto.Segment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.msgpack.MessagePack;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Denys Kovalenko on 7/18/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SegmentTemplateTest {
    private MessagePack messagePack = new MessagePack();

    @Test
    public void shouldPackAndUnpackSegmentWithNoPoints() throws IOException {
        List<Point> points = new ArrayList();
        Segment segment = new Segment(123, 12345, points);

        byte[] bytes = messagePack.write(segment, SegmentTemplate.getInstance());
        Segment resultSegment = messagePack.read(bytes, SegmentTemplate.getInstance());

        assertEquals(segment, resultSegment);
    }

    @Test
    public void shouldPackAndUnpackSegmentWithOnePoint() throws IOException {
        List<Point> points = new ArrayList(1);
        points.add(new Point(345.65, 976.45));
        Segment segment = new Segment(123, 12345, points);

        byte[] bytes = messagePack.write(segment, SegmentTemplate.getInstance());
        Segment resultSegment = messagePack.read(bytes, SegmentTemplate.getInstance());

        assertEquals(segment, resultSegment);
    }

    @Test
    public void shouldPackAndUnpackSegmentWithManyPoints() throws IOException {
        List<Point> points = new ArrayList(2);
        points.add(new Point(345.65, 976.45));
        points.add(new Point(-45.65, 36.97));
        Segment segment = new Segment(123, 12345, points);

        byte[] bytes = messagePack.write(segment, SegmentTemplate.getInstance());
        Segment resultSegment = messagePack.read(bytes, SegmentTemplate.getInstance());

        assertEquals(segment, resultSegment);
    }

    @Test(expected = NullPointerException.class)
    public void shouldPackAndUnpackSegmentWithNullPoints() throws IOException {
        Segment segment = new Segment(123, 12345, null);
        messagePack.write(segment, SegmentTemplate.getInstance());
    }

    @Test
    public void shouldFailureWithNullInput() throws IOException {
        byte[] bytes = messagePack.write(null);
        Segment resultSegment = messagePack.read(bytes, SegmentTemplate.getInstance());
        assertNull(resultSegment);
    }

}
