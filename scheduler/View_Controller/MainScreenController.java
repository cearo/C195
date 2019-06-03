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
    
    private SingleSelectionModel selectionModel;
    private boolean editMode = false;
    Parent root;

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
        Object selectedItem = selectionModel.getSelectedItem();
        /* *****TO DO*****
           I need to figure out how to get the text on the Add button
           to change when it's pressed.
        */

        if(selectedItem instanceof Tab) {
            String addBtnTxt = addButton.getText();
            AnchorPane pageContent = (AnchorPane) (
                    (Tab) selectedItem).getContent();
            ObservableList<Node> children = pageContent.getChildren();
            switch (addBtnTxt) {
                case "Add":
                    editMode = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setText("Save");
                        }
                    });
                    
                    children.forEach((child) -> {
                        if(child.isDisabled()) {
                            child.setDisable(false);
                        }
                        else if(!(child instanceof Label)){
                            child.setDisable(true);
                        }
                    });
                case "Save":
                    editMode = false;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setText("Add");
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
            }        
        }

//            editMode = true;
//            Object selectedItem = selectionModel.getSelectedItem();
//            if(selectedItem instanceof Tab) {
//                System.out.println(((Tab) selectedItem).getText());
//                AnchorPane pageContent = (AnchorPane) (
//                        (Tab) selectedItem).getContent();
//                ObservableList<Node> children = pageContent.getChildren();
//                children.forEach((child) -> {
//                    if(child.isDisabled()) {
//                        child.setDisable(false);
//                    }
//                });
//            }
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
        Boolean doesTabExist = false;
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
