/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author Cory
 */
public class ApplicationState {
    
    private static final String[] OPERATIONS = {"View", "Update", "Add"};
    private static boolean editMode;
    private static String currentOperation;
    private static String currentUser;
    private static int currUserId;
    private static TimeZone userTimeZone = null;
    private static Locale userLocale = null;
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
    
    public static void setCurrentOperation(String operation) 
                       throws IllegalArgumentException, IllegalStateException {
        
        switch(operation) {
            case "View":
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
                if(editMode) {
                    currentOperation = OPERATIONS[2];
                }
                else {
                    throw new IllegalStateException("Application State does"
                            + "not permit the Add operation. EditMode = " 
                            + editMode);
                }
                break;
                
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
    
    public static TimeZone getUserTimeZone() {
        
        
        if(userTimeZone == null) {
            userTimeZone = TimeZone.getDefault();
        }
        
        return userTimeZone;
    }
    
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
    
    public static Locale getLocale() {
        
        if(userLocale == null) {
            userLocale = Locale.getDefault(Locale.Category.FORMAT);
        }
        
        return userLocale;
    }
    
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
