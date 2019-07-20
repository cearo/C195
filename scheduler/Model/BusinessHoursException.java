package scheduler.Model;

/**
 *
 * @author Cory
 * This exception represents the business logic that appointments can only exist
 * within business hours.
 */
public class BusinessHoursException extends Exception {
    
    public BusinessHoursException(String message) {
        super(message);
    }
}
