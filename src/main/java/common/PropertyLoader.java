package common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 9:05 AM
 */
public class PropertyLoader {
    private static Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        try (InputStream input = PropertyLoader.class.getClassLoader().getResourceAsStream("redisConnection.properties")) {
            if (input == null) {
                throw new RuntimeException("Cannot find redisConnection.properties file");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static String getProperty(PropertyKeys propertyKey){
        return properties.getProperty(propertyKey.getKey());
    }

}
