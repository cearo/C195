/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import java.sql.*;


/**
 *
 * @author Cory
 */
public class SQLConnectionHandler {
    
    private Connection conn = null;
    
    public Connection getSqlConnection() {
        final String CONN_URL = "jdbc:mysql://localhost:3306/C195?" +
                                    "user=scheduler&password=Omgl33th4x";

        try {
            conn = DriverManager.getConnection(CONN_URL);
               
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        return conn;
  
    }
    
    public void closeSqlConnection() {
       try{
           conn.close();
       }
       catch(SQLException sqlEx) {
           sqlEx.printStackTrace();
       }
    }
    
    public ResultSet executeQuery(String query) {
        ResultSet result = null;
        
        if(conn == null) {
            this.getSqlConnection();
        }
        try { 
            Statement stmnt = this.conn.createStatement();
            result = stmnt.executeQuery(query);
        }
        catch(SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        return result;
    }
}
