package dal;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 10:12 AM
 */
@Test
public class RedisConnectivityTest {

   public void testConnection() throws Exception {
       // Just execute get/set simple commands to make sure that connection is ok
       String keyName = "key1";
       String valueName = "Hello World!";
       RedisCommandsManager.set(keyName, valueName);

       String actualValue = RedisCommandsManager.get(keyName);
       Assert.assertEquals(actualValue, valueName);
   }

}
