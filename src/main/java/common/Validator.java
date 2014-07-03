package common;

import dto.Point;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class Validator {

    public void validateParameters(String vin, Point point, long timestamp){
        // Null validation
        // Validate VIN
        if(vin == null) {
            throw new NullPointerException("VIN cannot be null");
        }

        // Validate Point
        if(point == null){
            throw new NullPointerException("Point cannot be null");
        }

        StringBuilder message = new StringBuilder();
        // Validate VIN
        if(vin.trim().isEmpty()){
            message.append("VIN cannot be empty. ");
        }

        // Validate timestamp
        if(timestamp <= 0){
            message.append("Timestamp cannot be negative or zero. ");
        }

        if(message.length() != 0){
            throw new IllegalArgumentException(message.toString());
        }
    }

    public void validateRoute(List<String> coordinates){
        if(coordinates == null){
            throw new NullPointerException("The route cannot be null");
        }
        if(coordinates.isEmpty()){
            throw new IllegalArgumentException("The route cannot be empty");
        }
    }
}
