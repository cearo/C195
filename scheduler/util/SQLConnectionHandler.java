package scheduler.util;

import java.sql.*;


/**
 *
 * @author Cory
 * This class handles the interactions to the SQL database
 */
public class SQLConnectionHandler {
    
    // The current Connection object for this instance
    private Connection conn = null;
    
    public Connection getSqlConnection() {
        // This URL to the database instance
        final String CONN_URL = "jdbc:mysql://52.206.157.109:3306/U04bTG?" +
                                    "user=U04bTG&password=53688196118";

        try {
            // Trying to get that connection
            conn = DriverManager.getConnection(CONN_URL);
               
        } catch (SQLException ex) {
           ex.printStackTrace();
        }
        return conn;
  
    }
    // Closes the current connection
    public void closeSqlConnection() {
       try{
           conn.close();
       }
       catch(SQLException sqlEx) {
           sqlEx.printStackTrace();
       }
    }
    // Executes queries that can be converted to a Statement. This will NOT
    // work for Prepared Statements.
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
