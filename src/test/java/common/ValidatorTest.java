package common;

import dto.Point;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Denys Kovalenko on 7/2/2014.
 */
@ContextConfiguration(locations = {"classpath:test-spring-config.xml"})
@RunWith( SpringJUnit4ClassRunner.class)
public class ValidatorTest {

    @Autowired
    private Validator validator;


    @Test
    public void validateParametersTest(){
        // First arg is incorrect (null)
        Exception firsAgrIncorrectCase = null;
        try {
            validator.validateParameters(null, new Point(23.4, -588.348), 1341434);
        } catch (Exception e) {
            firsAgrIncorrectCase = e;
        }
        assertTrue(firsAgrIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", firsAgrIncorrectCase.getMessage());

        // First arg is incorrect (empty)
        Exception firsAgrIncorrectCase2 = null;
        try {
            validator.validateParameters("", new Point(23.4, -588.348), 1341434);
        } catch (Exception e) {
            firsAgrIncorrectCase2 = e;
        }
        assertTrue(firsAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", firsAgrIncorrectCase2.getMessage());

        // Second arg is incorrect
        Exception secondAgrIncorrectCase = null;
        try {
            validator.validateParameters("1C4PJMDS3EW135024", null, 234);
        } catch (Exception e) {
            secondAgrIncorrectCase = e;
        }
        assertTrue(secondAgrIncorrectCase instanceof NullPointerException);
        assertEquals("Point cannot be null", secondAgrIncorrectCase.getMessage());

        // Third arg is incorrect
        Exception thirdAgrIncorrectCase = null;
        try {
            validator.validateParameters("1C4PJMDS3EW135024", new Point(23.4, -588.348), 0);
        } catch (Exception e) {
            thirdAgrIncorrectCase = e;
        }
        assertTrue(thirdAgrIncorrectCase instanceof IllegalArgumentException);
        assertEquals("Timestamp cannot be negative or zero", thirdAgrIncorrectCase.getMessage());

        Exception thirdAgrIncorrectCase2 = null;
        try {
            validator.validateParameters("1C4PJMDS3EW135024", new Point(23.4, -588.348), -10);
        } catch (Exception e) {
            thirdAgrIncorrectCase2 = e;
        }
        assertTrue(thirdAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("Timestamp cannot be negative or zero", thirdAgrIncorrectCase2.getMessage());

        // All arguments are incorrect
        Exception allAgrsIncorrectCase = null;
        try {
            validator.validateParameters(null, null, 0);
        } catch (Exception e) {
            allAgrsIncorrectCase = e;
        }
        assertTrue(allAgrsIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", allAgrsIncorrectCase.getMessage());

        // Different combinations
        Exception diffAgrsIncorrectCase = null;
        try {
            validator.validateParameters("1C4PJMDS3EW135024", null, -1);
        } catch (Exception e) {
            diffAgrsIncorrectCase = e;
        }
        assertTrue(diffAgrsIncorrectCase instanceof NullPointerException);
        assertEquals("Point cannot be null", diffAgrsIncorrectCase.getMessage());

        Exception diffAgrsIncorrectCase2 = null;
        try {
            validator.validateParameters("", null, 12);
        } catch (Exception e) {
            diffAgrsIncorrectCase2 = e;
        }
        assertTrue(diffAgrsIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", diffAgrsIncorrectCase2.getMessage());
    }


    @Test
    public void validateParametersSecondMethodTest(){
        // First arg is incorrect (null)
        Exception firsAgrIncorrectCase = null;
        try {
            validator.validateParameters(null, -1);
        } catch (Exception e) {
            firsAgrIncorrectCase = e;
        }
        assertTrue(firsAgrIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", firsAgrIncorrectCase.getMessage());

        // First arg is incorrect (empty)
        Exception firsAgrIncorrectCase2 = null;
        try {
            validator.validateParameters("", -1);
        } catch (Exception e) {
            firsAgrIncorrectCase2 = e;
        }
        assertTrue(firsAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", firsAgrIncorrectCase2.getMessage());

        // Second arg is incorrect (0)
        Exception firsAgrIncorrectCase3 = null;
        try {
            validator.validateParameters("VIN123", 0);
        } catch (Exception e) {
            firsAgrIncorrectCase3 = e;
        }
        assertTrue(firsAgrIncorrectCase3 instanceof IllegalArgumentException);
        assertEquals("Quantity must be in range between 1 and 20", firsAgrIncorrectCase3.getMessage());

        // Second arg is incorrect (-1)
        Exception firsAgrIncorrectCase4 = null;
        try {
            validator.validateParameters("VIN123", -1);
        } catch (Exception e) {
            firsAgrIncorrectCase4 = e;
        }
        assertTrue(firsAgrIncorrectCase4 instanceof IllegalArgumentException);
        assertEquals("Quantity must be in range between 1 and 20", firsAgrIncorrectCase4.getMessage());
    }


    @Test
    public void validateRouteTest(){
        // Negative use case 1 (null check)
        List<String> coordinates = null;
        Exception nullCaseCheck = null;
        try {
            validator.validateRoute(coordinates);
        } catch (Exception e) {
            nullCaseCheck = e;
        }
        assertTrue(nullCaseCheck instanceof NullPointerException);
        assertEquals("The route cannot be null", nullCaseCheck.getMessage());

        // Negative use case 2 (empty check)
        coordinates = new ArrayList<>();
        Exception emptyCaseCheck = null;
        try {
            validator.validateRoute(coordinates);
        } catch (Exception e) {
            emptyCaseCheck = e;
        }
        assertTrue(emptyCaseCheck instanceof IllegalArgumentException);
        assertEquals("The route cannot be empty", emptyCaseCheck.getMessage());

        // Positive use case
        coordinates.add("345.343:-765.54");
        validator.validateRoute(coordinates);
    }


    @Test
    public void validateVINTest(){
        // First arg is incorrect (null)
        Exception firsAgrIncorrectCase = null;
        try {
            validator.validateVin(null);
        } catch (Exception e) {
            firsAgrIncorrectCase = e;
        }
        assertTrue(firsAgrIncorrectCase instanceof NullPointerException);
        assertEquals("VIN cannot be null", firsAgrIncorrectCase.getMessage());

        // First arg is incorrect (empty)
        Exception firsAgrIncorrectCase2 = null;
        try {
            validator.validateVin(" ");
        } catch (Exception e) {
            firsAgrIncorrectCase2 = e;
        }
        assertTrue(firsAgrIncorrectCase2 instanceof IllegalArgumentException);
        assertEquals("VIN cannot be empty", firsAgrIncorrectCase2.getMessage());
    }
}
