package common;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 9:21 AM
 */
@Test
public class PropertyLoaderTest {


    public void loadPropertiesTest(){
        String host = PropertyLoader.getProperty(PropertyKeys.HOST);
        String port = PropertyLoader.getProperty(PropertyKeys.PORT);

        Assert.assertEquals(host, "127.0.0.1");
        Assert.assertEquals(port, "6379");
    }
}
