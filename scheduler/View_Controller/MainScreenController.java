/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
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
    private static boolean editMode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       selectionModel = tabPane.getSelectionModel();
       setEditMode(false);
    }    

    @FXML
    private void customersLinkHandler(ActionEvent event) {

        tabHandler("Customers");
        
    }

    @FXML
    private void appointmentsLinkHandler(ActionEvent event) {

        tabHandler("Appointments");
    }

    @FXML
    private void usersLinkHandler(ActionEvent event) {

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

        if(selectedItem instanceof Tab) {
            editModeHandler(event);
        }
    }

    @FXML
    private void editButtonHandler(ActionEvent event) {
        Object selectedItem = selectionModel.getSelectedItem();
        if(selectedItem instanceof Tab) {
            editModeHandler(event);
        }

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
    // Enables/Disables Edit Mode for the Application
    public static void setEditMode(boolean mode) {
        editMode = mode;
    }
    
   /*
        This method enables disabled form elements and disables enabled ones.
        The one exception are the Labels as they should always be enabled.
    
        ***********************************************************************
        Upon application initialization, all editable form controls are set to
        disabled. Once Edit Mode is enabled, all editable form controls become
        enabled and the List Views become disabled as a current record is being
        edited/added therefore, the user should not be able to select another
        element.
        ***********************************************************************
    
        Considering the relationship with editMode and the fact that editMode is
        a boolean, I had considered utilizing its state to pass as an arg to
        child.setDisable() however, that felt more clever than clear. For 
        clarity's sake, I explicity stated the boolean values 
        passed to the method.
    
    */
    public void setChildElementsDisabled(ObservableList<Node> children) {
        
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
    }
    
    public void editModeHandler(ActionEvent event) {
        // The current selected UI object
        Object selectedItem = selectionModel.getSelectedItem();
        // Selecting the content in the tab, which is contained in an AnchorPane
        AnchorPane pageContent = (AnchorPane) ((Tab) selectedItem).getContent();
        // Collecting the individual elements on the page
        ObservableList<Node> children = pageContent.getChildren();
        
        if(event.getSource() instanceof Button) {
            Button buttonPressed = (Button) event.getSource();
            String buttonId = buttonPressed.getId();
            
            switch (buttonId) {
                /*
                   The lack of a break; statement in the addButton case is 
                   intentional. The Add and Edit buttons share very similar
                   actions. The Add button has the additional responsibility of
                   clearing the form data to prepare for a new addition. Other 
                   than that, the buttons perform exactly the same:
                        - set editMode to true
                        - Update the Add Button's ID and Text to Save
                        - Update the Edit Button's ID and Text to Cancel
                        - Disable the Delete Button
                */
                case "addButton":
                    clearFormData(children);
                case "editButton":
                    System.out.println("In edit case");
                    setEditMode(true);
          
                    setChildElementsDisabled(children);
                    
                    // Tapping into the main application thread
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            // Add button becomes Save button
                            addButton.setText("Save");
                            addButton.setId("saveButton");
                            // Edit button becomes Cancel button
                            editButton.setText("Cancel");
                            editButton.setId("cancelButton");
                            // Can't use Delete button
                            deleteButton.setDisable(true);
                        }
                    });
                break;
                case "saveButton":
                    setEditMode(false);
                    
                    setChildElementsDisabled(children);
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setText("Add");
                            addButton.setId("addButton");
                            editButton.setText("Edit");
                            editButton.setId("editButton");
                            deleteButton.setDisable(false);
                        }
                    });
                break;
                case "cancelButton":
                    // Checking for Edit Mode to be enabled is a little 
                    // redundant as the Cancel Button only appears in Edit Mode
                    // but it's safe and logical, I feel.
                    if(editMode) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Discard Unsaved Changes?");
                        alert.setHeaderText("**YOU MAY HAVE UNSAVED CHANGES!***");
                        alert.setContentText("All unsaved changes will be"
                                + " discarded if you cancel. "
                                + "\nARE YOU SURE you want to cancel?");
                        ButtonType yesButton = new ButtonType("Yes");
                        ButtonType noButton = new ButtonType("No");
                        alert.getButtonTypes().setAll(yesButton, noButton);
                        Optional<ButtonType> result = alert.showAndWait();
                        
                        if(result.get() == yesButton) {
                            setEditMode(false);
                            setChildElementsDisabled(children);
                            
                        Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            addButton.setText("Add");
                            addButton.setId("addButton");
                            editButton.setText("Edit");
                            editButton.setId("editButton");
                            deleteButton.setDisable(false);
                        }
                    });
                        }
                        else{
                            alert.close();
                        }
                    }
                    break;
                    
        }
    }
    }
    public void clearFormData(ObservableList<Node> children) {
        children.forEach((child) -> {
                        if(child instanceof TextField) {
                            ((TextField) child).clear();
                            System.out.println("Clearing TextFields");
                        }
                        else if(child instanceof CheckBox) {
                            ((CheckBox) child).setSelected(false);
                            System.out.println("Clearing Check Boxes");
                        }
                        else if(child instanceof DatePicker) {
                            ((DatePicker) child).setValue(null);
                            System.out.println("Clearing Date Picker");
                        }
                        else if(child instanceof ChoiceBox) {
                            ((ChoiceBox) child).getSelectionModel().
                                                clearSelection();
                            System.out.println("Clearing Choice Boxes");
                        }
                    });
    }
}
