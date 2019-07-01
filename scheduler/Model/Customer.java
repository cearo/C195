/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import scheduler.util.SQLConnectionHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import org.omg.CORBA.portable.ApplicationException;
import scheduler.util.ApplicationState;

/**
 *
 * @author Cory
 */
public class Customer {
    
    private IntegerProperty id;
    private StringProperty customerName;
    private IntegerProperty isActive;
    private IntegerProperty addressId;
    private Address address;
    
    public Customer(int id, String customerName, int isActive, int addressId) {
        this.id = new SimpleIntegerProperty(id);
        this.customerName = new SimpleStringProperty(customerName);
        this.isActive = new SimpleIntegerProperty(isActive);
        this.addressId = new SimpleIntegerProperty(addressId);
        this.address = null;
    }
    
    public int getId() {
        return this.id.get();
    }
    
    private void setId(int id) {
        this.id.set(id);
    }
    
    public IntegerProperty idProperty() {
        return id;
    }
    
    public String getName() {
        return this.customerName.get();
    }
    
    public void setName(String name) {
        this.customerName.set(name);
    }
    
    public StringProperty customerNameProperty() {
        return customerName;
    }
    
    public int getActiveState() {
        return this.isActive.get();
    }
    
    public void setActiveState(int isActive) {
        this.isActive.set(isActive);
    }
    
    public IntegerProperty isActiveProperty() {
        return isActive;
    }
    
    public int getAddressId() {
        return this.addressId.get();
    }
    
    private void setAddressId(int id) {
        this.addressId.set(id);
    }
    
    public IntegerProperty addressIdProperty() {
        return addressId;
    }
    
    public static Customer addCustomerRecord(ObservableList<Node> children) 
                                            throws IllegalStateException,
                                                   SQLException {
        Customer cust = null;
        String appOperation = ApplicationState.getCurrentOperation();
        String currUser = ApplicationState.getCurrentUser();
        Address customerAddress = null;
        
        if(appOperation.equals("Add")) {
            SQLConnectionHandler sql = new SQLConnectionHandler();
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

            
            for(int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
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
            String addressInsert =
                    "INSERT into address(address, address2, cityId,"
                    + "postalCode, phone, createDate, createdBy)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?);";
            String getCityId = String.format("SELECT cityId "
                                             + "FROM city "
                                             + "WHERE city = '%s';", city);
            String sqlTime = "SELECT NOW();";
            
            Connection conn = sql.getSqlConnection();
            
            ResultSet cityIdResult = sql.executeQuery(getCityId);
            
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
                PreparedStatement pstmnt = 
                        conn.prepareStatement(addressInsert, 
                                              Statement.RETURN_GENERATED_KEYS);
                pstmnt.setString(1, addr1);
                pstmnt.setString(2, addr2);
                pstmnt.setInt(3, cityId);
                pstmnt.setString(4, zip);
                pstmnt.setString(5, phone);
                pstmnt.setTimestamp(6, timestamp);
                pstmnt.setString(7, currUser);
                
                int rowsAffected = pstmnt.executeUpdate();
                
                if(rowsAffected == 1) {
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
            String addCustomerQuery = 
                    "INSERT INTO customer (customerName, addressId, active,"
                    + "createDate, createdBy)"
                    + "VALUES(?, ?, ?, ?, ?)";
            
            try {
                PreparedStatement pstmnt = 
                        conn.prepareStatement(addCustomerQuery, 
                                Statement.RETURN_GENERATED_KEYS);
                pstmnt.setString(1, name);
                pstmnt.setInt(2, addrId);
                pstmnt.setInt(3, active);
                pstmnt.setTimestamp(4, timestamp);
                pstmnt.setString(5, currUser);
                
                int rowsAffected = pstmnt.executeUpdate();
                
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
    
    public void updateCustomerRecord(ObservableList<Node> children) 
            throws IllegalStateException, SQLException {
        
        String appOperation = ApplicationState.getCurrentOperation();
        String currUser = ApplicationState.getCurrentUser();
        Address custAddress = this.getCustomerAddress();
        
        if(appOperation.equals("Update")) {
            for(int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
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
                    boolean valuesChanged = false;
                    
                    switch(childId) {
                        
                        case "CustCity":
                             if(!currCityName.equals(selectedValue)) {
                                 this.address.setCityName(selectedValue);
                                 currCityName = this.address.getCityName();
                                 valuesChanged = true;
                             }
                            break;
                        
                        case "CustCountry":
                            if(!currCountryName.equals(selectedValue)) {
                                this.address.setCountryName(selectedValue);
                                currCountryName = this.address.getCountryName();
                                valuesChanged = true;
                            }
                            break;
                    }
                    
                    if(valuesChanged) {
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
            String updateAddress = "UPDATE address SET address=?, address2=?,"
                    + "cityId=?, postalCode=?, phone=?, lastUpdate=?,"
                    + "lastUpdateBy=? WHERE addressId = ?";
            String updateCustomer = "UPDATE customer SET customerName=?,"
                    + "addressId=?, active=?, lastUpdate=?, lastUpdateBy=? "
                    + "WHERE customerId = ?";
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
            PreparedStatement addrPstmnt = conn.prepareStatement(updateAddress);
            
            addrPstmnt.setString(1, this.address.getAddressField1());
            addrPstmnt.setString(2, this.address.getAddressField2());
            addrPstmnt.setInt(3, this.address.getCityId());
            addrPstmnt.setString(4, this.address.getPostalCode());
            addrPstmnt.setString(5, this.address.getPhoneNumber());
            addrPstmnt.setTimestamp(6, timestamp);
            addrPstmnt.setString(7, currUser);
            addrPstmnt.setInt(8, this.address.getId());
            
            PreparedStatement custPstmnt = 
                    conn.prepareStatement(updateCustomer);
            
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
    
    public Address getCustomerAddress() {
        
        Address result = null;
        
        if(this.address == null) {
            int customerId = this.getId();
            int addressId = this.getAddressId();

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
