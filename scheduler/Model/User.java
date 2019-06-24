/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

/**
 *
 * @author Cory
 */
public class User {
    
    private String username;
    private int userId;
    private boolean isActive;
    
    public User(String username, int userId, boolean activeState) {
        this.username = username;
        this.userId = userId;
        this.isActive = activeState;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public int getUserId() {
        return this.userId;
    }
    
    private void setUserId(int id) {
        this.userId = id;
    }
    
    public boolean isActive() {
        return this.isActive;
    }
    
    public void setActiveState(boolean activeState) {
        this.isActive = activeState;
    }
}
