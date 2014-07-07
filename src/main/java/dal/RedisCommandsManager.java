package dal;

import common.PropertyKeys;
import common.PropertyLoader;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.List;
import java.util.Map;

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


    public void set(String key, String value) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key, value);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public void hSet(String key, String field, String value) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public String get(String key) throws Exception {
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

    public Object eval(String script) throws Exception {
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

    public Long incr(String key) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(key);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public void rPush(String key, String value) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.rpush(key, value);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public void lPush(String key, String value) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.lpush(key, value);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public List<byte[]> lRange(String key, long start, long end) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.lrange(key.getBytes(), start, end);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }

    public void delete(String key) throws Exception {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } catch (JedisConnectionException e) {
            returnBrokenResource(jedis);
            throw new Exception("Redis connection refused");
        } finally {
            returnResource(jedis);
        }
    }


    private void returnBrokenResource(Jedis jedis){
        jedisPool.returnBrokenResource(jedis);
    }

    private void returnResource(Jedis jedis){
        jedisPool.returnResource(jedis);
    }
}
