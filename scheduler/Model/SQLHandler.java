/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cory
 */
public class SQLHandler {
    private static final String CONN_URL = "jdbc:mysql://localhost:3306/C195?" +
                                    "user=scheduler&password=Omgl33th4x";
    Connection conn = null;
    public static void main(String[] args) {
        
    }
    
    public Connection getSqlConnection() {

        try {
            conn = DriverManager.getConnection(CONN_URL);
               
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        return conn;
  
    }
    


    
}
