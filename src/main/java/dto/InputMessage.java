package dto;


/**
 * Created by Denys Kovalenko on 7/9/2014.
 */
public class InputMessage {

    public void setVin( String vin ) {
        this.vin = vin;
    }

    public void setPoint( Point point ) {
        this.point = point;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    private String vin;
    private Point  point;
    private long   timestamp;

    public InputMessage() {
    }

    public InputMessage( String vin, Point point, long timestamp ) {
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
