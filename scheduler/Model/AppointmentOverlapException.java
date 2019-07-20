package scheduler.Model;

/**
 *
 * @author Cory
 * This exception represents the business logic that appointments cannot overlap
 */
public class AppointmentOverlapException extends Exception {
    
    public AppointmentOverlapException(String message) {
        super(message);
    }
}
