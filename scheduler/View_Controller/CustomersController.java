/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import scheduler.util.DataHandler;
import scheduler.util.SQLConnectionHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 * This class controls the interactions between the user and the Customers.fxml
 * UI layer.
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
//    @FXML
//    private TableView<?> appTable;
//    @FXML 
//    private TableColumn<?, ?> appDateCol;
//    @FXML
//    private TableColumn<?, ?> appTimeCol;
    @FXML
    private TextField phoneField;
    @FXML
    private ChoiceBox<String> cityChoice;
//    @FXML
//    private ChoiceBox<?> stateChoice;
    @FXML
    private ChoiceBox<String> countryChoice;
    // These represent the results of queries to obtain all cities and countries
    // from the database.
    private ResultSet allCitiesResult = null;
    private ResultSet allCountriesResult = null;
    private ObservableList CUSTOMERS = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CUSTOMERS = DataHandler.getCustomers();
        // Setting the Cell Value Factories to use the Java Beans Properties
        // in the Customer class so the field info will display in the TableView
        custIdCol.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        custNameCol.setCellValueFactory(
                new PropertyValueFactory<>("customerName"));
        // Binding the TableView to the underlying data source
        custTable.setItems(CUSTOMERS);
        // Selecting the first Customer object
        custTable.getSelectionModel().selectFirst();
        // Getting that selected object
        Customer cust = custTable.getSelectionModel().getSelectedItem();
        // Filling the form data
        fillCustomerForm(cust);
        // This Lambda adds a Listener that will fill the Customer form data
        // when the selection is changed by the end user.
        custTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if(newSelection != null) {
                        Customer selected = newSelection;
                        fillCustomerForm(selected);
                    }
                });
    }    
    
    // This method filles the form data for Customers.fxml
    private void fillCustomerForm(Customer obj) {
        // Getting the Customer's address info
        Address customerAddress = obj.getCustomerAddress();
        // Checking to see if I have all the cities and countries from the
        // database
        if(allCitiesResult == null || allCountriesResult == null) {
            // If not, let's go get it.
            String allCities = "SELECT city FROM city ORDER BY city;";
            String allCounrties = 
                    "SELECT country FROM country ORDER BY country;";
            SQLConnectionHandler sql = new SQLConnectionHandler();
            
            allCitiesResult = sql.executeQuery(allCities);
            allCountriesResult = sql.executeQuery(allCounrties);
            // Populating the cityChoice and countryChoice Choice Boxes with
            // the cities and countries from the DB
            try {
                while(allCitiesResult.next()) {
                    cityChoice.getItems().add(allCitiesResult.getString("city"));
                }
                while(allCountriesResult.next()) {
                    countryChoice.getItems().add(
                            allCountriesResult.getString("country"));
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
        // If the customer is active, check the Check Box
        if(obj.getActiveState() == 1) {
            activeCheckBox.setSelected(true);
        }
        
        phoneField.setText(customerAddress.getPhoneNumber());
        addrField.setText(customerAddress.getAddressField1());
        addr2Field.setText(customerAddress.getAddressField2());
        zipField.setText(customerAddress.getPostalCode());
        
    }
    
}
