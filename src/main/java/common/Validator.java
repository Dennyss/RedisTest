package common;

import dto.Point;

import java.util.List;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
public class Validator {


    public void validateParameters(String vin, Point point, long timestamp) {
        // Validate VIN
        validateVin(vin);

        // Validate Point
        if (point == null) {
            throw new NullPointerException("Point cannot be null");
        }

        // Validate timestamp
        if (timestamp <= 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative or zero");
        }
    }


    public void validateParameters(String vin, int quantity) {
        // Validate VIN
        validateVin(vin);

        // Validate quantity
        if (quantity <= 0 || quantity > 20) {
            throw new IllegalArgumentException("Quantity must be in range between 1 and 20");
        }
    }


    public void validateRoute(List<String> coordinates) {
        if (coordinates == null) {
            throw new NullPointerException("The route cannot be null");
        }
        if (coordinates.isEmpty()) {
            throw new IllegalArgumentException("The route cannot be empty");
        }
    }


    public void validateVin(String vin) {
        // Validate VIN
        if (vin == null) {
            throw new NullPointerException("VIN cannot be null");
        }

        if (vin.trim().isEmpty()) {
            throw new IllegalArgumentException("VIN cannot be empty");
        }
    }
}
