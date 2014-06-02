package dal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;


/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 10:12 AM
 */
@Test
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class RedisConnectivityTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StringRedisTemplate template;

    @Test
   public void testConnection() throws Exception {
       // Just execute get/set simple commands to make sure that connection is ok
       String keyName = "key1";
       String valueName = "Hello World!";
       RedisCommandsManager.set(keyName, valueName);

       String actualValue = RedisCommandsManager.get(keyName);
       Assert.assertEquals(actualValue, valueName);
   }

    @Test
    public void simpleScriptingTest() throws Exception {
        String result = (String) RedisCommandsManager.eval("local msg = \"Hello, world!\" return msg");
        Assert.assertEquals(result, "Hello, world!");
    }

    @Test
    public void scriptFileUsageTest() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("helloWorld.lua"));
        script.setResultType(String.class);
        String result = template.execute(script, Collections.<String>emptyList());
        Assert.assertEquals(result, "Hello, world!");
    }




}
