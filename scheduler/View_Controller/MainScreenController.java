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
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.Scheduler;

/**
 * FXML Controller class
 *
 * @author Cory
 */
public class MainScreenController implements Initializable {

    @FXML
    private SplitPane mainWindow;
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
    
    // selectionModel is used to gather info about what the user is doing
    private SingleSelectionModel selectionModel;
    /* 
        editMode maintains the state of the application.
        Certain actions will be disabled while in Edit Mode including:
        - Using the Edit button
        - Using the Delete button
        - Switching tabs
        - Closing a tab
        -- A Discard Changes prompt will appear which will take the application
           out of Edit Mode before clearing the form.
        - Exiting the application
        -- Again, Discard Changes prompt.
    */
    private boolean editMode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       selectionModel = tabPane.getSelectionModel();
       editMode = false;
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

        tabHandler("Customers");
        
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
        tabHandler("Appointments");
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
         tabHandler("Users");
    }

    @FXML
    private void reportsLinkHandler(ActionEvent event) {
        
        tabHandler("Reports");
    }

    @FXML
    private void addButtonHandler(ActionEvent event) {
        // The current selected UI object
        Object selectedItem = selectionModel.getSelectedItem();
        // Selecting the content in the tab, which is contained in an AnchorPane
        AnchorPane pageContent = (AnchorPane) ((Tab) selectedItem).getContent();
        // Collecting the individual elements on the page
        ObservableList<Node> children = pageContent.getChildren();

        if(selectedItem instanceof Tab) {
            // On initial load this should be "Add"
            String addBtnTxt = addButton.getText();
 
            switch (addBtnTxt) {
                case "Add":
                    editMode = true;
                               
                    children.forEach((child) -> {
                        if(child.isDisabled()) {
                            // Enabling all disabled children
                            child.setDisable(false);
                        }
                        // I don't want to disable Labels
                        else if(!(child instanceof Label)){
                            // Disabling List Views
                            child.setDisable(true);
                        }
                    });
                    // Tapping into the main application thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // Add button becomes Save button
                            addButton.setText("Save");
                            // Can't use Edit button
                            editButton.setDisable(true);
                            // Can't use Delete button
                            deleteButton.setDisable(true);
                        }
                    });
                break;    
                case "Save":
                    editMode = false;

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setText("Add");
                            editButton.setDisable(false);
                            deleteButton.setDisable(false);
                        }
                    });
                    children.forEach((child) -> {
                       if(!child.isDisabled() && !(child instanceof Label)) {
                           child.setDisable(true);
                       }
                       else {
                           child.setDisable(false);
                       }
                    });
                break;
            }        
        }
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
    
    private void tabHandler(String tabName) {
        boolean doesTabExist = false;
        ObservableList<Tab> tabList = tabPane.getTabs();
        Tab tab;
        for(Tab i : tabList) {
            String tabId = i.getId();
            if(tabId != null && tabId.equals(tabName)) {
                doesTabExist = true;
                tab = i;
                selectionModel.select(tab);
                break;
            }
        }
        
        if(!doesTabExist) {
            tab = new Tab(tabName);
            tab.setId(tabName);
            FXMLLoader loader = new FXMLLoader();
            try {
                AnchorPane root = loader.load(getClass().getResource(
                        Scheduler.BASE_FOLDER_PATH + tabName + ".fxml"));
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
