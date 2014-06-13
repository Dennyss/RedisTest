package sharding;

import redis.clients.jedis.Protocol;
import redis.clients.util.MurmurHash;

import java.io.UnsupportedEncodingException;

/**
 * Created by Denys Kovalenko on 6/13/2014.
 *
 * The instance of this class represents the one Redis shard or DB unit.
 *
 * Sharding logic.
 * Input parameters (to make a decision is will this shard apply/update current state ot not):
 *  - vin;
 *  - shardNumber in shard matrix table;
 *  - total shards number (defined here as a constant).
 *
 */
public class RedisShard {
    private int shardNumber;
    private String host;
    private String port;

    private long selfShardHash;
    private static final int TOTAL_SHARDS_NUMBER = 1024;

    public RedisShard(int shardNumber, String host, String port) {
        this.shardNumber = shardNumber;
        this.host = host;
        this.port = port;

        initSelfHash();
    }

    private void initSelfHash() {
        selfShardHash = (Long.MIN_VALUE/(TOTAL_SHARDS_NUMBER/2)) * shardNumber;
    }


    public void process(String vin, String state) throws Exception {
        if(isThisShardRelatedData(vin)){
            // todo: update state into this shard redis database
            // getConnection(port, host).hset(vin, state);
        }
    }


    private boolean isThisShardRelatedData(String vin) throws Exception {
        // 1. Get bytes
        byte[] vinBytes = vin.getBytes(Protocol.CHARSET);

        // 2. Get VIN hash
        MurmurHash murmurHash = new MurmurHash();
        long vinHash = murmurHash.hash(vinBytes);

        // 3. Compare VIN hash with self hash
        if (selfShardHash == vinHash) {
            return true;
        }

        return false;
    }


}
