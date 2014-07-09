package dto;

import org.msgpack.annotation.Message;
import processing.PolylineEncoder;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/8/2014.
 */
@Message
public class Segment {
    private long startTimestamp;
    private long endTimestamp;
    private List<Point> segmentPoints;
    private String encodedSegment;

    public Segment() {
    }

    public Segment(long startTimestamp, long endTimestamp, List<Point> segmentPoints) {
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.segmentPoints = segmentPoints;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public long getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(long endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    public List<Point> getSegmentPoints() {
        return segmentPoints;
    }

    public void setSegmentPoints(List<Point> segmentPoints) {
        this.segmentPoints = segmentPoints;
    }

    public String getEncodedSegment() {
        // I guess that encoding too fast that we can encode segment every time when get invokes.
        // In this way we will always have updated encoded segment
        return PolylineEncoder.encodeSegment(segmentPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        if (endTimestamp != segment.endTimestamp) return false;
        if (startTimestamp != segment.startTimestamp) return false;
        if (encodedSegment != null ? !encodedSegment.equals(segment.encodedSegment) : segment.encodedSegment != null)
            return false;
        if (segmentPoints != null ? !segmentPoints.equals(segment.segmentPoints) : segment.segmentPoints != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTimestamp ^ (startTimestamp >>> 32));
        result = 31 * result + (int) (endTimestamp ^ (endTimestamp >>> 32));
        result = 31 * result + (segmentPoints != null ? segmentPoints.hashCode() : 0);
        result = 31 * result + (encodedSegment != null ? encodedSegment.hashCode() : 0);
        return result;
    }
}
