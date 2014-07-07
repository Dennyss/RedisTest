package common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 9:21 AM
 */
public class PropertyLoaderTest {


    @Test
    public void loadPropertiesTest(){
        String host = PropertyLoader.getProperty(PropertyKeys.HOST);
        String port = PropertyLoader.getProperty(PropertyKeys.PORT);

//        assertEquals(host, "172.17.34.126");
        assertEquals("localhost", host);
        assertEquals("6379", port);
    }
}
