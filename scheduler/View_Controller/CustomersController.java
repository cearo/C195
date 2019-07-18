/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.Model.Address;
import scheduler.Model.Customer;
import scheduler.util.SQLConnectionHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 */
public class CustomersController implements Initializable {

    @FXML
    private TableView<Customer> custTable;
    @FXML
    private TableColumn<Customer, Integer> custIdCol;
    @FXML
    private TableColumn<Customer, String> custNameCol;
    @FXML
    private TextField idField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addrField;
    @FXML
    private TextField addr2Field;
    @FXML
    private CheckBox activeCheckBox;
    @FXML
    private TextField zipField;
    @FXML
    private TableView<?> appTable;
    @FXML 
    private TableColumn<?, ?> appDateCol;
    @FXML
    private TableColumn<?, ?> appTimeCol;
    @FXML
    private TextField phoneField;
    @FXML
    private ChoiceBox<String> cityChoice;
    @FXML
    private ChoiceBox<?> stateChoice;
    @FXML
    private ChoiceBox<String> countryChoice;
    
    private final ObservableList custNames =
            FXCollections.observableArrayList();
    
    private final ObservableList CUSTOMERS = 
            FXCollections.observableArrayList();
    
    private ResultSet allCitiesResult = null;
    private ResultSet allCountriesResult = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("In CustomersController");
        String allCustomers = "SELECT * FROM customer ORDER BY customerId;";
        SQLConnectionHandler sql = new SQLConnectionHandler();
        ExecutorService service = null;
        
        try {
            service = Executors.newSingleThreadExecutor();
            
            Future<ResultSet> threadResult = 
                    service.submit(() -> sql.executeQuery(allCustomers));
            
            ResultSet result = threadResult.get();
            
            while(result.next()) {
                    try {
                        int id = result.getInt("customerId");
                        String name = result.getString("customerName");
                        int active = result.getInt("active");
                        int custAddId = result.getInt("addressId");
                        Customer cust = new Customer(id, name, active, 
                                                                     custAddId);
                        cust.getCustomerAddress();

                        CUSTOMERS.add(cust);
                        custNames.add(cust.getName());
                    }
                    catch(SQLException SqlEx) {
                        SqlEx.printStackTrace();
                    }
                
            }
        }
        catch(InterruptedException InterruptEx) {
            System.out.println("Thread interrupted");
        }
        catch(ExecutionException ExeEx) {
            ExeEx.printStackTrace();
        }
        catch(SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        finally {
            sql.closeSqlConnection();
            if(service != null) service.shutdown();
        }
        
        custIdCol.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(
                new PropertyValueFactory<>("customerName"));
        custTable.setItems(CUSTOMERS);
        
        custTable.getSelectionModel().selectFirst();
        Customer cust = custTable.getSelectionModel().getSelectedItem();
        fillCustomerForm(cust);
        
        custTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if(newSelection != null) {
                        Customer selected = newSelection;
                        fillCustomerForm(selected);
                    }
                });
    }    
    
    private void fillCustomerForm(Customer obj) {
        
        Address customerAddress = obj.getCustomerAddress();
        
        if(allCitiesResult == null || allCountriesResult == null) {
            
            String allCities = "SELECT city FROM city ORDER BY city;";
            String allCounrties = 
                    "SELECT country FROM country ORDER BY country;";
            SQLConnectionHandler sql = new SQLConnectionHandler();
            
            allCitiesResult = sql.executeQuery(allCities);
            allCountriesResult = sql.executeQuery(allCounrties);
            try {
                while(allCitiesResult.next()) {
                    cityChoice.getItems().add(allCitiesResult.getString("city"));
//                    cityChoice.setValue(customerAddress.getCityName());
                }
                while(allCountriesResult.next()) {
                    countryChoice.getItems().add(
                            allCountriesResult.getString("country"));
//                    countryChoice.setValue(
//                            customerAddress.getCountryName());
                }
            }
            catch(SQLException SQLEx) {
                SQLEx.printStackTrace();
            }
        }
        cityChoice.setValue(customerAddress.getCityName());
        countryChoice.setValue(customerAddress.getCountryName());
        
        idField.setText(Integer.toString(obj.getId()));
        nameField.setText(obj.getName());
        
        if(obj.getActiveState() == 1) {
            activeCheckBox.setSelected(true);
        }
        
        phoneField.setText(customerAddress.getPhoneNumber());
        addrField.setText(customerAddress.getAddressField1());
        addr2Field.setText(customerAddress.getAddressField2());
        zipField.setText(customerAddress.getPostalCode());
        
    }
    
    public ObservableList getCustomersList() {
        return this.CUSTOMERS;
    }
//    
//    public TableView<Customer> getCustomersTableView() {
//        return this.custTable;
//    }
//    
//    public void refreshCustomersTableView() {
//        this.custTable.refresh();
//    }
//    
//    public Customer getSelectedCustomer() {
//        return this.custTable.getSelectionModel().getSelectedItem();
//    }
}
