package common;

/**
 * User: denys.kovalenko
 * Date: 5/23/14
 * Time: 9:14 AM
 */
public enum PropertyKeys {
    HOST("redis.host");

    private String propertyKey;

    PropertyKeys(String propertyKey){
        this.propertyKey = propertyKey;
    }

    public String getKey(){
        return propertyKey;
    }

}
