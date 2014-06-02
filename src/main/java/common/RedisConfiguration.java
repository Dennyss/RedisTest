package common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * Created with IntelliJ IDEA.
 * User: denys.kovalenko
 * Date: 5/30/14
 * Time: 10:57 AM
 * To change this template use File | Settings | File Templates.
 */
//@Configuration
//@ComponentScan
public class RedisConfiguration {

//    @Bean
//    public StringRedisTemplate getSpringRedisTemplate(){
//        JedisConnectionFactory connectionFactory = new JedisConnectionFactory();
//
//        connectionFactory.setHostName(PropertyLoader.getProperty(PropertyKeys.HOST));
//        connectionFactory.setPort(Integer.parseInt(PropertyLoader.getProperty(PropertyKeys.PORT)));
//
//        return new StringRedisTemplate(connectionFactory);
//    }
}
