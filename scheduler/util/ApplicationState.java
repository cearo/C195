package scheduler.util;

import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Cory
 * This class holds state information for the current application runtime.
 */
public class ApplicationState {
    // The available application actions
    private static final String[] OPERATIONS = {"View", "Update", "Add"};
    // Whether the application is in an edit state
    private static boolean editMode;
    // What is the user currently trying to do?
    private static String currentOperation;
    // Who is currently logged in?
    private static String currentUser;
    private static int currUserId;
    // What's the time zone and locale of the logged in user?
    private static TimeZone userTimeZone = null;
    private static Locale userLocale = null;
    // What's the DB time zone?
    private static TimeZone dbTimeZone = null;
    
    
    public static boolean getEditMode() {
        return editMode;
    }
    
    public static void setEditMode(boolean mode) {
        editMode = mode;
    }
    
    public static String getCurrentOperation() {
        return currentOperation;
    }
    // The application only has three available operations: Add, Update, View
    // If a state other than those three were to be entered, 
    // an IllegalArgumentException will be thrown. If the Operation is updated
    // to Add or Update but the application isn't in edit mode, an 
    // IllegalStateException will be thrown.
    public static void setCurrentOperation(String operation) 
                       throws IllegalArgumentException, IllegalStateException {
        // Which operation are we setting?
        switch(operation) {
            
            case "View":
                // View can only be set if the application is not in edit mode
                if(!editMode) {
                    currentOperation = OPERATIONS[0];
                }
                else {
                    throw new IllegalStateException("Application State does"
                            + "not permit the View operation. EditMode = " 
                            + editMode);
                }
                break;
                
            case "Update":
                // Update can only be set if the appplication is in edit mode
                if(editMode) {
                    currentOperation = OPERATIONS[1];
                }
                else {
                    throw new IllegalStateException("Application State does"
                            + "not permit the Update operation. EditMode = " 
                            + editMode);
                }
                break;
                
            case "Add":
                // Add can only be set if the application is in edit mode
                if(editMode) {
                    currentOperation = OPERATIONS[2];
                }
                else {
                    throw new IllegalStateException("Application State does"
                            + "not permit the Add operation. EditMode = " 
                            + editMode);
                }
                break;
            // Something other than Add, Update, View is being passed.    
            default:
                throw new IllegalArgumentException(operation + " is not a"
                        + " valid application operation.");
        }
    }
    
    public static String getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentUser(String user) {
        currentUser = user;
    }
    
    public static int getCurrUserId() {
        return currUserId;
    }
    // There can't be a user ID of 0 so this setter validates the passed value
    // and throws an IllegalArgumentException if 0 is attempted.
    public static void setCurrUserId(int id) throws IllegalArgumentException {
        
        if(id != 0) {
           currUserId = id;
        }
        else {
            String err = "User ID cannot be 0.";
            IllegalArgumentException ex = new IllegalArgumentException(err);
            throw ex;
        }
    }
    // This getter will call the setter if the user's time zone is null
    public static TimeZone getUserTimeZone() {
        
        
        if(userTimeZone == null) {
            userTimeZone = TimeZone.getDefault();
        }
        
        return userTimeZone;
    }
    // The user's time zone cannot be null
    public static void setUserTimeZone(TimeZone timeZone) 
            throws IllegalArgumentException {
        
        if(timeZone != null) {
            userTimeZone = timeZone;
        }
        
        else {
            String err = "Time Zone cannot be null.";
            IllegalArgumentException ex = new IllegalArgumentException(err);
            throw ex;
        }
    }
    // This getter get's the user's locale if it hasn't been captured already
    public static Locale getLocale() {
        
        if(userLocale == null) {
            userLocale = Locale.getDefault(Locale.Category.FORMAT);
        }
        
        return userLocale;
    }
    // This setter validates that null isn't being passed as that's not allowed
    public static void setLocale(Locale loc) throws IllegalArgumentException {
        
        if(loc != null) {
            userLocale = loc;
        }
        else {
            String err = "Locale cannot be null.";
            IllegalArgumentException ex = new IllegalArgumentException(err);
            throw ex;
        }
    }
    
    public static TimeZone getDatabaseTimeZone() {
        return dbTimeZone;
    }
    // The database timezone cannot be null
    public static void setDatabaseTimezone(TimeZone tz) 
            throws IllegalArgumentException {
        
        if(tz != null) {
            dbTimeZone = tz;
        }
        
        else {
            String err = "Database TimeZone cannot be null.";
            IllegalArgumentException ex = new IllegalArgumentException(err);
            throw ex;
        }
    }
}
