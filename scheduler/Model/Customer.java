package scheduler.Model;

import scheduler.util.SQLConnectionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import scheduler.util.ApplicationState;

/**
 *
 * @author Cory
 * This class represents a Customer in the application and is modeled after 
 * the customer table in the database.
 */
public class Customer {
    
    // Used Java Beans Properties instead of standard primitives to make it 
    // easier to display data on the app.
    private final IntegerProperty ID;
    private final StringProperty CUSTOMER_NAME;
    private final IntegerProperty IS_ACTIVE;
    private final IntegerProperty ADDRESS_ID;
    private Address address;
    
    public Customer(int id, String customerName, int isActive, int addressId) {
        this.ID = new SimpleIntegerProperty(id);
        this.CUSTOMER_NAME = new SimpleStringProperty(customerName);
        this.IS_ACTIVE = new SimpleIntegerProperty(isActive);
        this.ADDRESS_ID = new SimpleIntegerProperty(addressId);
        this.address = null;
    }
    
    public int getId() {
        return this.ID.get();
    }
    
    private void setId(int id) {
        this.ID.set(id);
    }
    
    public IntegerProperty idProperty() {
        return ID;
    }
    
    public String getName() {
        return this.CUSTOMER_NAME.get();
    }
    
    public void setName(String name) {
        this.CUSTOMER_NAME.set(name);
    }
    
    public StringProperty customerNameProperty() {
        return CUSTOMER_NAME;
    }
    
    public int getActiveState() {
        return this.IS_ACTIVE.get();
    }
    
    public void setActiveState(int isActive) {
        this.IS_ACTIVE.set(isActive);
    }
    
    public IntegerProperty isActiveProperty() {
        return IS_ACTIVE;
    }
    
    public int getAddressId() {
        return this.ADDRESS_ID.get();
    }
    
    private void setAddressId(int id) {
        this.ADDRESS_ID.set(id);
    }
    
    public IntegerProperty addressIdProperty() {
        return ADDRESS_ID;
    }
    
    // This method takes in an ObservableList of nodes from the form fields 
    // which contain data to create the new Customer record in the database,
    // create a new customer object.
    public static Customer addCustomerRecord(ObservableList<Node> children) 
                                            throws IllegalStateException,
                                                   SQLException {
        // The Customer object to be returned
        Customer cust = null;
        // Getting the app state to validate
        String appOperation = ApplicationState.getCurrentOperation();
        String currUser = ApplicationState.getCurrentUser();
        Address customerAddress = null;
        
        // Validating that the application is in add state
        if(appOperation.equals("Add")) {
            SQLConnectionHandler sql = new SQLConnectionHandler();
            
            // Customer fields
            int custId = 0;
            String name = null;
            int active = 0;
            String phone = null;
            String addr1 = null;
            String addr2 = null;
            String city = null;
            String zip = null;
            String country;
            int cityId = 0;
            Timestamp timestamp = null;
            int addrId = 0;

            // Begin grabbing form data
            for(int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
                // Since I can't know what type of object I'm working with
                // and each object has different methods for getting the form
                // data, I'm validating the object type, casting it to the child
                // and calling the appropriate method.
                if(child instanceof TextField) {
                    
                    String childText = ((TextField) child).getText();
                    
                    
                    switch(childId) {
                        
                        case "CustId":
                            custId = Integer.parseInt(childText);
                            break;
                            
                        case "CustName":
                            name = childText;
                            break;
                            
                        case "CustPhone":
                            phone = childText;
                            break;
                            
                        case "CustAddr1":
                            addr1 = childText;
                            break;
                            
                        case "CustAddr2":
                            addr2 = childText;
                            break;
                            
                        case "CustZip":
                            zip = childText;
                            break;
                    }
                    
                }
                else if(child instanceof CheckBox) {
                    
                    boolean isSelected = ((CheckBox) child).isSelected();
                    
                    if(isSelected) {
                        active = 1;
                    }
                    else {
                        active = 0;
                    }
                }
                else if(child instanceof ChoiceBox) {
                    
                    Object selectedValue = ((ChoiceBox) child).getValue();

                    switch(childId) {
                        
                        case "CustCity":
                            city = (String) selectedValue;
                            break;
                        
                        case "CustCountry":
                            country = (String) selectedValue;
                            break;
                    }
                }
            }
            // The SQL Insert string to add a new address record
            String addressInsert =
                    "INSERT into address(address, address2, cityId,"
                    + "postalCode, phone, createDate, createdBy, lastUpdateBy)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?);";
            // The SQL string to get a city ID for creating a new address
            String getCityId = String.format("SELECT cityId "
                                             + "FROM city "
                                             + "WHERE city = '%s';", city);
            // Getting the time of the SQL server
            String sqlTime = "SELECT NOW();";
            
            Connection conn = sql.getSqlConnection();
            
            // Executing statement to get the city ID
            ResultSet cityIdResult = sql.executeQuery(getCityId);
            
            // Executing the statement to get the SQL time
            ResultSet sqlTimeResult = sql.executeQuery(sqlTime);
            
            try {
                if(cityIdResult.next()) {
                    cityId = cityIdResult.getInt("cityId");
                }
                
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err = String.format("There was an error retrieving"
                        + "the cityId. cityId = %d", cityId);
                SQLException appEx = new SQLException(err);
                throw appEx;
            }
            
            try {
                if(sqlTimeResult.next()) {
                    timestamp = sqlTimeResult.getTimestamp(1);
                }
                
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err = String.format("There was an error retrieving"
                        + "the time from the database. Time = %s", timestamp);
                SQLException appEx = new SQLException(err);
                throw appEx;
            }
            
            try { 
                // Prepared statement to insert the new address which returns
                // the auto generated primary key
                PreparedStatement pstmnt = 
                        conn.prepareStatement(addressInsert, 
                                              Statement.RETURN_GENERATED_KEYS);
                // Configuring the prepared statement
                pstmnt.setString(1, addr1);
                pstmnt.setString(2, addr2);
                pstmnt.setInt(3, cityId);
                pstmnt.setString(4, zip);
                pstmnt.setString(5, phone);
                pstmnt.setTimestamp(6, timestamp);
                pstmnt.setString(7, currUser);
                pstmnt.setString(8, currUser);
                
                // Executing prepared statement
                int rowsAffected = pstmnt.executeUpdate();
                
                if(rowsAffected == 1) {
                    // Getting the generated primary key
                    ResultSet addrIdResult = pstmnt.getGeneratedKeys();
                    if(addrIdResult.next()) {
                        addrId = addrIdResult.getInt(1);
                    }
                    else {
                        String err = String.format(
                                "There was an error getting the generated"
                                + " keys for the new address. AddrId = %s", 
                                addrId);
                        SQLException appEx = new SQLException(err);
                        throw appEx;
                    }
                }
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
            
            // SQL statement string to insert a new customer record
            String addCustomerQuery = 
                    "INSERT INTO customer (customerName, addressId, active,"
                    + "createDate, createdBy, lastUpdateBy)"
                    + "VALUES(?, ?, ?, ?, ?, ?)";
            
            try {
                // Prepared Statement to add new customer record and return
                // the auto generated primary key
                PreparedStatement pstmnt = 
                        conn.prepareStatement(addCustomerQuery, 
                                Statement.RETURN_GENERATED_KEYS);
                // Configuring the prepared statement
                pstmnt.setString(1, name);
                pstmnt.setInt(2, addrId);
                pstmnt.setInt(3, active);
                pstmnt.setTimestamp(4, timestamp);
                pstmnt.setString(5, currUser);
                pstmnt.setString(6, currUser);
                
                int rowsAffected = pstmnt.executeUpdate();
                
                // Getting the generated keys
                if(rowsAffected == 1) {
                    ResultSet custIdResult = pstmnt.getGeneratedKeys();
                    if(custIdResult.next()) {
                        custId = custIdResult.getInt(1);
                    }
                }
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
            finally {
                sql.closeSqlConnection();
            }
            // Creating new Customer object
            cust = new Customer(custId, name, active, addrId);
            cust.getCustomerAddress();
        }
        
        else {
            String errMsg = "Application state does not permit adding a new"
                    + "customer record."
                    + "\nEdit Mode = " + ApplicationState.getEditMode()
                    + "\nOperation = " + ApplicationState.getCurrentOperation();
            IllegalStateException ex = new IllegalStateException(errMsg);
            throw ex;
        }
        return cust;
    }
    
    // This method updates the object from which it is called and updates the
    // corresponding SQL record.
    public void updateCustomerRecord(ObservableList<Node> children) 
            throws IllegalStateException, SQLException {
        // Getting application operation to validate
        String appOperation = ApplicationState.getCurrentOperation();
        // Getting the current logged in user
        String currUser = ApplicationState.getCurrentUser();
        Address custAddress = this.getCustomerAddress();
        
        // Validating that the app is in Update mode
        if(appOperation.equals("Update")) {
            for(int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
                // Since I can't know what type of object I'm working with
                // and each object has different methods for getting the form
                // data, I'm validating the object type, casting it to the child
                // and calling the appropriate method.
                if(child instanceof TextField) {
                    
                    String childText = ((TextField) child).getText();
                    
                    
                    switch(childId) {
                            
                        case "CustName":
                            this.setName(childText);
                            break;
                            
                        case "CustPhone":
                             custAddress.setPhoneNumber(childText);
                            break;
                            
                        case "CustAddr1":
                            custAddress.setAddressField1(childText);
                            break;
                            
                        case "CustAddr2":
                            custAddress.setAddressField2(childText);
                            break;
                            
                        case "CustZip":
                            custAddress.setPostalCode(childText);
                            break;
                    }
                    
                }
                else if(child instanceof CheckBox) {
                    
                    boolean isSelected = ((CheckBox) child).isSelected();
                    
                    if(isSelected) {
                        this.setActiveState(1);
                    }
                    else {
                        this.setActiveState(0);
                    }
                }
                else if(child instanceof ChoiceBox) {
                    // Since none of my objects store city and country info
                    // I have to go to the database to get the info.
                    String selectedValue = 
                            (String) ((ChoiceBox) child).getValue();
                    String queryTemplate = 
                            "SELECT ci.cityId AS cityId, ci.city AS city,"
                            + "co.countryId as countryId, co.country AS country"
                            + " FROM city ci INNER JOIN country co"
                            + " ON(ci.countryId = co.countryId)"
                            + " WHERE ci.city = '%s';";
                    String currCityName = this.address.getCityName();
                    String currCountryName = this.address.getCountryName();
                    // This will be used to determine whether I need to submit
                    // my SQL query.
                    boolean valuesChanged = false;
                    
                    switch(childId) {
                        
                        case "CustCity":
                            // Determining if the value in the form field
                            // is different from what the object has stored
                             if(!currCityName.equals(selectedValue)) {
                                 this.address.setCityName(selectedValue);
                                 currCityName = this.address.getCityName();
                                 valuesChanged = true;
                             }
                            break;
                        // Determining if the value in the form field
                        // is different from what the object has stored
                        case "CustCountry":
                            if(!currCountryName.equals(selectedValue)) {
                                this.address.setCountryName(selectedValue);
                                currCountryName = this.address.getCountryName();
                                valuesChanged = true;
                            }
                            break;
                    }
                    
                    // If the form and object values don't match, I need to go
                    // to SQL to get the city and country IDs
                    if(valuesChanged) {
                        // Formatting the query string with the city name
                        String cityAndCountryInfo = 
                                String.format(queryTemplate, currCityName);
                        SQLConnectionHandler sql = new SQLConnectionHandler();
                        ResultSet result = sql.executeQuery(cityAndCountryInfo);
                        if(result.next()) {
                            try {
                                int cityId = result.getInt("cityId");
                                int countryId = result.getInt("countryId");
                                
                                this.address.setCityId(cityId);
                                this.address.setCountryId(countryId);
                            }
                            catch(SQLException SqlEx) {
                                String err = "There was an error when fetching"
                                        + "City and Country information.";
                                SQLException sqlEx = new SQLException(err);
                                throw sqlEx;
                            }
                            finally {
                                sql.closeSqlConnection();
                            }
                        }
                    }
                }
            }
            // The SQL update query string for the customer's address
            String updateAddress = "UPDATE address SET address=?, address2=?,"
                    + "cityId=?, postalCode=?, phone=?, lastUpdate=?,"
                    + "lastUpdateBy=? WHERE addressId = ?";
            // The SQL update query string for the Customer record
            String updateCustomer = "UPDATE customer SET customerName=?,"
                    + "addressId=?, active=?, lastUpdate=?, lastUpdateBy=? "
                    + "WHERE customerId = ?";
            // The current time of the SQL server
            String sqlTime = "SELECT NOW();";
            Timestamp timestamp = null;
            SQLConnectionHandler sql = new SQLConnectionHandler();
            
            ResultSet timeRes = sql.executeQuery(sqlTime);
            
            if(timeRes.next()) {
                try {
                    timestamp = timeRes.getTimestamp(1);
                }
                catch(SQLException SqlEx) {
                    String err = "There was an error when fetching the SQL"
                            + "time.";
                    SQLException ex = new SQLException(err);
                    throw ex;
                }
                finally {
                    sql.closeSqlConnection();
                }
            }
            
            Connection conn = sql.getSqlConnection();
            // Prepared statement used to execute update address record
            PreparedStatement addrPstmnt = conn.prepareStatement(updateAddress);
            // Configuring the prepared statement
            addrPstmnt.setString(1, this.address.getAddressField1());
            addrPstmnt.setString(2, this.address.getAddressField2());
            addrPstmnt.setInt(3, this.address.getCityId());
            addrPstmnt.setString(4, this.address.getPostalCode());
            addrPstmnt.setString(5, this.address.getPhoneNumber());
            addrPstmnt.setTimestamp(6, timestamp);
            addrPstmnt.setString(7, currUser);
            addrPstmnt.setInt(8, this.address.getId());
            
            // Prepared statement used to execute update customer record
            PreparedStatement custPstmnt = 
                    conn.prepareStatement(updateCustomer);
            // Configuring the prepared statement
            custPstmnt.setString(1, this.getName());
            custPstmnt.setInt(2, this.address.getId());
            custPstmnt.setInt(3, this.getActiveState());
            custPstmnt.setTimestamp(4, timestamp);
            custPstmnt.setString(5, currUser);
            custPstmnt.setInt(6, this.getId());
            
            try {
                addrPstmnt.executeUpdate();
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err ="There was an error updating the Address record.";
                SQLException ex = new SQLException(err);
                throw ex;
            }
            try {
                 custPstmnt.executeUpdate();
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err = "There was an error updating the Customer record.";
                SQLException ex = new SQLException(err);
                throw ex;
            }
            
        }
        else {
            String errMsg = "Application state does not permit adding a new"
                    + "customer record."
                    + "\nEdit Mode = " + ApplicationState.getEditMode()
                    + "\nOperation = " + ApplicationState.getCurrentOperation();
            IllegalStateException ex = new IllegalStateException(errMsg);
            throw ex;
        }
    }
    
    // Deleting this customer record from SQL
    public void deleteCustomerRecord() {
         int custId = this.getId();
         String deleteQuery = String.format("DELETE FROM customer " 
                            + "WHERE customerId = %d", custId);
         
         SQLConnectionHandler sql = new SQLConnectionHandler();
         
         try {
             Connection conn = sql.getSqlConnection();
             Statement stmnt = conn.createStatement();
             stmnt.execute(deleteQuery);
         }
         catch(SQLException SqlEx) {
             SqlEx.printStackTrace();
         }
         finally {
             sql.closeSqlConnection();
         }
    }
    
    // This getter will check to see if the current address for this customer
    // is null and if so, reach out to SQL to get the customer's address info.
    public Address getCustomerAddress() {
        
        Address result = null;
        
        if(this.address == null) {
            int customerId = this.getId();
            int addressId = this.getAddressId();
            
            // The SQL query string to get the customer info
            String getAddressInfo = String.format("SELECT addr.address,"
                    + "addr.address2, addr.cityId, addr.postalCode, addr.phone,"
                    + "ci.city, co.countryId, co.country "
                    + "FROM address addr INNER JOIN city ci "
                    + "ON addr.cityId = ci.cityId INNER JOIN country co "
                    + "ON ci.countryId = co.countryId "
                    + "WHERE addr.addressId =%d", addressId);

            SQLConnectionHandler sql = new SQLConnectionHandler();

            try {

                Connection conn = sql.getSqlConnection();
                Statement stmnt = conn.createStatement();
                ResultSet custAddr = stmnt.executeQuery(getAddressInfo);

                if(custAddr.next()){

                    String addr = custAddr.getString("addr.address");
                    String addr2 = custAddr.getString("addr.address2");
                    int addrCityId = custAddr.getInt("addr.cityId");
                    String addrPostalCode = custAddr.getString("addr.postalCode");
                    String addrPhn = custAddr.getString("addr.phone");
                    String addrCityName = custAddr.getString("ci.city");
                    String addrCntryName = custAddr.getString("co.country");
                    int addrCntryId = custAddr.getInt("co.countryId");

                    result = new Address(addressId, addr, addr2, addrCityId, 
                                         addrPostalCode, addrPhn, addrCityName,
                                         addrCntryId, addrCntryName);
                }
            }
            catch(SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
            finally {
                sql.closeSqlConnection();
            }
            this.address = result;
        }
        
        else {
            result = this.address;
        }
        return result;
    }
    
}
