package dto;

import org.msgpack.annotation.Message;

/**
 * Created by Denys Kovalenko on 7/9/2014.
 */
@Message
public class InputMessage {
    private String vin;
    private Point point;
    private long timestamp;

    public InputMessage() {
    }

    public InputMessage(String vin, Point point, long timestamp) {
        this.vin = vin;
        this.point = point;
        this.timestamp = timestamp;
    }


    public String getVin() {
        return vin;
    }

    public Point getPoint() {
        return point;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
