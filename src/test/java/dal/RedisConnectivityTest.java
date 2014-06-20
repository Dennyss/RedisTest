package dal;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.junit.Assert.assertEquals;


/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 10:12 AM
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith( SpringJUnit4ClassRunner.class)
public class RedisConnectivityTest {

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    RedisCommandsManager redisCommandsManager;

    @Test
   public void testConnection() throws Exception {
       // Just execute get/set simple commands to make sure that connection is ok
       String keyName = "key1";
       String valueName = "Hello World!";
        redisCommandsManager.set(keyName, valueName);

       String actualValue = redisCommandsManager.get(keyName);
       assertEquals(actualValue, valueName);
   }

    @Test
    public void simpleScriptingTest() throws Exception {
        String result = (String) redisCommandsManager.eval("local msg = \"Hello, world!\" return msg");
        assertEquals(result, "Hello, world!");
    }

    @Test
    public void scriptFileUsageTest() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("helloWorld.lua"));
        script.setResultType(String.class);
        String result = template.execute(script, Collections.<String>emptyList());
        assertEquals(result, "Hello, world!");
    }

}
