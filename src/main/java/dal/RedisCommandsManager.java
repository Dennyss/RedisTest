package dal;

import common.PropertyKeys;
import common.PropertyLoader;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * User: denys.kovalenko
 * Date: 5/22/14
 * Time: 5:33 PM
 *
 * @see //https://github.com/xetorthio/jedis/wiki/Getting-started
 */
public class RedisCommandsManager {
    private static String host = PropertyLoader.getProperty(PropertyKeys.HOST);
    private static JedisPool jedisPool = new JedisPool(new JedisPoolConfig(), host);

    private RedisCommandsManager() {
    }

    public static void closeAllConnections(){
        jedisPool.destroy();
    }

    public static void set(String key, String value) throws Exception {
        Jedis jedis = jedisPool.getResource();    // todo: to understand do I need to do this per each command? or with a batch of commands ....
        try {
            jedis.set(key, value);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public static String get(String key) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(key);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public static Object eval(String script) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.eval(script);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }


    private static void returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }

    private static void returnResource(Jedis jedis){
        jedisPool.returnResource(jedis);
    }
}
