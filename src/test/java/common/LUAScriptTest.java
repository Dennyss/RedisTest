package common;

import dal.RedisCommandsManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This test created for Redis LUA scripting learning purpose
 *
 * Resources used for that:
 * http://www.lua.org/pil/contents.html
 * http://www.redisgreen.net/blog/2013/03/18/intro-to-lua-for-redis-programmers/
 * http://redis.io/commands/eval
 *
 * Created by Denys Kovalenko on 6/11/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith( SpringJUnit4ClassRunner.class)
public class LUAScriptTest {

    @Autowired
    private StringRedisTemplate template;

    @Autowired
    RedisCommandsManager redisCommandsManager;

    @Test
    public void scriptArgsVarsStringsLearningTest() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("argsVarsStringTest.lua"));
        script.setResultType(String.class);
        List<String> keys = new ArrayList<String>();
        keys.add("Key1");
        keys.add("Key2");
        String result = template.execute(script, keys, "Arg1", "Arg2", "Arg3");

        assertEquals("Key1: Key1, Key2: Key2, Arg1: Arg1, Arg2: Arg2, Arg3: Arg3", result);
    }

    @Test
    public void scriptHSetAndIncrTest() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("hSetAndIncrTest.lua"));
        script.setResultType(Long.class);
        List<String> keys = new ArrayList<String>();
        keys.add("links:counter");
        keys.add("links:urls");
        Long result = template.execute(script, keys, "http://google.com/");

        assertTrue(result instanceof Long);
    }

    @Test
    public void testT() throws Exception {
        redisCommandsManager.set("Key1", "10");
        Long incrResult = redisCommandsManager.incr("Key1");
        String getResult = redisCommandsManager.get("Key1");

        assertEquals(new Long(11), incrResult);
        assertEquals("11", getResult);
    }

    @Test
    public void scriptConditionalLogicTest() throws Exception {
        String key = "NewKey";
        String field = "Field1";
        String value = "10";

        redisCommandsManager.hSet(key, field, value);

        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("conditionalLogicTest.lua"));
        script.setResultType(Long.class);
        List<String> keys = new ArrayList<String>();
        keys.add(key);
        Long result = template.execute(script, keys, field);

        assertEquals(new Long(11), result);
    }



}
