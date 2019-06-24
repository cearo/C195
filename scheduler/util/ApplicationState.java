/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

/**
 *
 * @author Cory
 */
public class ApplicationState {
    
    private static final String[] OPERATIONS = {"View", "Update", "Add"};
    private static boolean editMode;
    private static String currentOperation;
    private static String currentUser;
    
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
}
