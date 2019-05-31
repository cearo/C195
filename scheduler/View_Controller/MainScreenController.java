/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;
import scheduler.Scheduler;

/**
 * FXML Controller class
 *
 * @author Cory
 */
public class MainScreenController implements Initializable {

    @FXML
    private VBox menu;
    @FXML
    private Hyperlink customersLink;
    @FXML
    private Hyperlink appointmentsLink;
    @FXML
    private Hyperlink usersLink;
    @FXML
    private Hyperlink reportsLink;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button deleteButton1;
    
    private SingleSelectionModel selectionModel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       selectionModel = tabPane.getSelectionModel();
    }    

    @FXML
    private void customersLinkHandler(ActionEvent event) {
//        Boolean doesTabExist = false;
//        tabList = tabPane.getTabs();
//        
//        for(Tab i : tabList) {
//            String tabId = i.getText();
//            if(tabId.equals("Customers")) {
//                doesTabExist = true;
//                break;
//            }
//        }
//        
//        if(!doesTabExist) {
//            tab = new Tab("Customers");
//            tab.setId("Customers");
//            Label label = new Label("Customers");
//            tab.setContent(label);
//            FXMLLoader loader = new FXMLLoader();
//            tabPane.getTabs().add(tab);
//            selectionModel.select(tab);
//        }

        tabCreator("Customers");
        
    }

    @FXML
    private void appointmentsLinkHandler(ActionEvent event) {
//        Boolean doesTabExist = false;
//        tabList = tabPane.getTabs();
//        
//        for(Tab i : tabList) {
//            String tabTitle = i.getText();
//            if(tabTitle.equals("Appointments")) {
//                doesTabExist = true;
//                break;
//            }
//        }
//        
//        if(!doesTabExist) {
//            tab = new Tab("Appointments");
//            tab.setId("Appointments");
//            Label label = new Label("Appointments");
//            tab.setContent(label);
//            tabPane.getTabs().add(tab);
//            selectionModel.select(tab);
//        }
        
    }

    @FXML
    private void usersLinkHandler(ActionEvent event) {
//        Boolean doesTabExist = false;
//        tabList = tabPane.getTabs();
//        
//        for(Tab i : tabList) {
//            String tabTitle = i.getText();
//            if(tabTitle.equals("Users")) {
//                doesTabExist = true;
//                break;
//            }
//        }
//        
//        if(!doesTabExist) {
//            tab = new Tab("Users");
//            tab.setId("Users");
//            Label label = new Label("Users");
//            tab.setContent(label);
//            tabPane.getTabs().add(tab);
//            selectionModel.select(tab);
//        }
//        
    }

    @FXML
    private void reportsLinkHandler(ActionEvent event) {
        
        
    }

    @FXML
    private void addButtonHandler(ActionEvent event) {
    }

    @FXML
    private void editButtonHandler(ActionEvent event) {
    }

    @FXML
    private void deleteButtonHandler(ActionEvent event) {
    }

    @FXML
    private void closeButtonHandler(ActionEvent event) {
    }
    
    private void tabCreator(String tabName) {
        Boolean doesTabExist = false;
        ObservableList<Tab> tabList = tabPane.getTabs();
        
        for(Tab i : tabList) {
            String tabId = i.getId();
            if(tabId.equals(tabName)) {
                doesTabExist = true;
                break;
            }
        }
        
        if(!doesTabExist) {
            Tab tab = new Tab(tabName);
            tab.setId(tabName);
            FXMLLoader loader = new FXMLLoader();
            try {
//                loader.setLocation(getClass().getResource(
//                    Scheduler.BASE_FOLDER_PATH + tabName + ".fxml"));
                Parent root = (Parent) FXMLLoader.load(this.getClass().
                        getResource(Scheduler.BASE_FOLDER_PATH + tabName + ".fxml"));
                tab.setContent(root);
            }
            catch(IOException ioEX) {
                System.out.println("Issue loading " + tabName + ".fxml");
                ioEX.printStackTrace();
            }
            tabPane.getTabs().add(tab);
            selectionModel.select(tab);
        }
    }
}
