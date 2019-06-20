/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.Model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

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
    
    public void updateCustomerRecord(int id, 
                                     String customerName, int isActive) 
    {
        this.setId(id);
        this.setName(customerName);
        this.setActiveState(isActive);
        
        int currentId = this.getId();
        String currentName = this.getName();
        int currentActiveState = this.getActiveState();
        
        String updateQuery = 
              String.format("UPDATE customer SET customerName=%s, active=%d"
              + "WHERE id=%d;", currentName, currentActiveState, 
              currentId);
        
        SQLConnectionHandler sql = new SQLConnectionHandler();
        
        try {
            Connection conn = sql.getSqlConnection();
            Statement stmnt = conn.createStatement();
            stmnt.executeUpdate(updateQuery);
        }
        catch(SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        finally {
            sql.closeSqlConnection();
        }
    }
    
    public void deleteCustomerRecord() {
         int id = this.getId();
         String deleteQuery = String.format("DELETE FROM customer " 
                            + "WHERE id=%d", id);
         
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
