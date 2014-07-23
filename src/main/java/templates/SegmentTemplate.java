package templates;

import dto.Point;
import dto.Segment;
import org.msgpack.MessageTypeException;
import org.msgpack.packer.Packer;
import org.msgpack.template.AbstractTemplate;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/18/2014.
 */
public class SegmentTemplate extends AbstractTemplate<Segment> {
    private static final SegmentTemplate instance = new SegmentTemplate();

    public static SegmentTemplate getInstance() {
        return instance;
    }

    private SegmentTemplate() {
    }


    @Override
    public void write(Packer packer, Segment segment, boolean required) throws IOException {
        if (segment == null) {
            if (required) {
                throw new MessageTypeException("Attempted to write null");
            }
            packer.writeNil();
            return;
        }

        packer.writeArrayBegin(3);
        packer.write(segment.getStartTimestamp());
        packer.write(segment.getEndTimestamp());

        packer.writeArrayBegin(segment.getSegmentPoints().size());
        for(Point point : segment.getSegmentPoints()){
            packer.writeArrayBegin(2);
            packer.write(point.getLatitude());
            packer.write(point.getLongitude());
            packer.writeArrayEnd();
        }
        packer.writeArrayEnd();
    }

    @Override
    public Segment read(Unpacker unpacker, Segment toSegment, boolean required) throws IOException {
        if (!required && unpacker.trySkipNil()) {
            return null;
        }
        if (toSegment == null) {
            toSegment = new Segment();
        }

        unpacker.readArrayBegin();
        toSegment.setStartTimestamp(unpacker.readLong());
        toSegment.setEndTimestamp(unpacker.readLong());

        int size = unpacker.readArrayBegin();
        List<Point> points = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            unpacker.readArrayBegin();
            double lat = unpacker.readDouble();
            double lon = unpacker.readDouble();
            points.add(new Point(lat, lon));
            unpacker.readArrayEnd();
        }
        unpacker.readArrayEnd();
        toSegment.setSegmentPoints(points);
        unpacker.readArrayEnd();

        return toSegment;
    }

}
