package common;

import junit.framework.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

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
@Test
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
public class LUAScriptTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private StringRedisTemplate template;

    @Test
    public void scriptArgsVarsStringsLearningTest() {
        DefaultRedisScript<String> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("ArgsVarsStringTest.lua"));
        script.setResultType(String.class);
        List<String> keys = new ArrayList<String>();
        keys.add("Key1");
        keys.add("Key2");
        String result = template.execute(script, keys, "Arg1", "Arg2", "Arg3");

        Assert.assertEquals("Key1: Key1, Key2: Key2, Arg1: Arg1, Arg2: Arg2, Arg3: Arg3", result);
    }

}
