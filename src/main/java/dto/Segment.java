package dto;

import org.msgpack.annotation.Message;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/8/2014.
 */
@Message
public class Segment {
    private long startTimestamp;
    private long endTimestamp;
    private List<Point> segmentPoints;

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
        return encodeSegment(segmentPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Segment segment = (Segment) o;

        if (endTimestamp != segment.endTimestamp) return false;
        if (startTimestamp != segment.startTimestamp) return false;
        if (segmentPoints != null ? !segmentPoints.equals(segment.segmentPoints) : segment.segmentPoints != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTimestamp ^ (startTimestamp >>> 32));
        result = 31 * result + (int) (endTimestamp ^ (endTimestamp >>> 32));
        result = 31 * result + (segmentPoints != null ? segmentPoints.hashCode() : 0);
        return result;
    }

    private String encodeSegment(List<Point> points) {
        Assert.notNull(points, "Points should not be null");

        StringBuilder encodedRoute = new StringBuilder();
        int latitudePrevious = 0;
        int longitudePrevious = 0;

        for (Point point : points) {
            // Multiply Point coordinates by 1e5
            int latitudeCurrent = (int) Math.floor(point.getLatitude() * 1e5);
            int longitudeCurrent = (int) Math.floor(point.getLongitude() * 1e5);

            // Calculate the one piece of route, or diff between previous position and current
            int latitudeDiff = latitudeCurrent - latitudePrevious;
            int longitudeDiff = longitudeCurrent - longitudePrevious;

            // Save current position for the next processing
            latitudePrevious = latitudeCurrent;
            longitudePrevious = longitudeCurrent;

            encodeCoordinate(encodedRoute, latitudeDiff);
            encodeCoordinate(encodedRoute, longitudeDiff);
        }

        return encodedRoute.toString();
    }

    private void encodeCoordinate(StringBuilder encodedRoute, int coordinate) {
        int sgn_num = coordinate << 1;
        if (coordinate < 0) {
            sgn_num = ~(sgn_num);
        }

        while (sgn_num >= 0x20) {
            int nextValue = (0x20 | (sgn_num & 0x1f)) + 63;
            encodedRoute.append((char) (nextValue));
            sgn_num >>= 5;
        }
        sgn_num += 63;
        encodedRoute.append((char) (sgn_num));
    }
}
