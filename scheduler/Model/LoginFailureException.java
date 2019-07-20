package scheduler.Model;

/**
 *
 * @author Cory
 * This exception represents the business logic requiring a user be able to 
 * enter a valid username/password to enter the application.
 */
public class LoginFailureException extends Exception {
    
    public LoginFailureException(String message) {
        super(message);
    }
}
