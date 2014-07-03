package processing;

import common.Validator;
import dal.RedisCommandsManager;
import dto.Point;
import org.msgpack.MessagePack;
import org.msgpack.template.Templates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class DefaultRouteSegmentProcessor implements RouteSegmentProcessor {
    // We will save only last point timestamp for vin. We don't need to know point coordinates.
    public static final String ROUT_SEGMENTS_KEY = "routeSegments:";

    @Autowired
    private RedisCommandsManager redisCommandsManager;
    @Autowired
    private PolylineEncoder polylineEncoder;
    @Autowired
    private Validator validator;
    @Autowired
    private StringRedisTemplate template;


    public void applyPoint(String vin, double latitude, double longitude, long timestamp) throws Exception {
        applyPoint(vin, new Point(latitude, longitude), timestamp);
    }


    @Override
    public void applyPoint(String vin, Point point, long timestamp) throws Exception {
        validator.validateParameters(vin, point, timestamp);

        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("processing/processSegments.lua"));
        script.setResultType(String.class);

        String result = template.execute(script, Collections.<String>emptyList(), vin, String.valueOf(point.getLatitude()),
                String.valueOf(point.getLongitude()), String.valueOf(timestamp));

        System.out.println(result);
    }


    public List<String> getEncodedSegments(String vin, int quantity) throws Exception {
        validator.validateParameters(vin, quantity);
        // Return from DB <quantity> segments by vin, unpack and encode
        List<String> result = redisCommandsManager.lRange(ROUT_SEGMENTS_KEY + vin, 0, quantity - 1);
        return null; //unpackAndEncode(redisCommandsManager.lRange(ROUT_SEGMENTS_KEY + vin, 0, quantity - 1));
    }


    @Override
    public List<String> getEncodedSegments(String vin) throws Exception {
        validator.validateVin(vin);

        return getEncodedSegments(vin, 1);
    }


    private List<String> unpackAndEncode(List<byte[]> segments) {
        List<String> unpackedAndEncoded = new ArrayList(segments.size());
        for (byte[] segment : segments) {
            unpackedAndEncoded.add(polylineEncoder.encodeRoute(unpack(segment)));
        }
        return unpackedAndEncoded;
    }


    private List<String> unpack(byte[] segment) {
        MessagePack messagePack = new MessagePack();

        List<String> data = null;
        try {
            data = messagePack.read(segment, Templates.tList(Templates.TString));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String startTimestamp = null;
        String endTimestamp = null;
        List<String> unpacked = new ArrayList(data.size() - 2);
        for (int i = 0; i < data.size(); i++) {
            if (i == 0) {
                startTimestamp = data.get(i);
            } else if (i == 1) {
                endTimestamp = data.get(i);
            } else {
                unpacked.add(data.get(i));
            }
        }
        return unpacked;
    }
}
