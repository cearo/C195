package scheduler.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import scheduler.Model.Appointment;
import scheduler.Model.AppointmentOverlapException;
import scheduler.Model.BusinessHoursException;
import scheduler.Scheduler;
import scheduler.Model.Customer;
import scheduler.util.ApplicationState;
import scheduler.util.DataHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 * This class controls the users interactions with the mainscreen container for
 * the application. Since this application never leaves this view, this is
 * the primary controller for user actions.
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
    private Hyperlink reportsLink;
    @FXML
    private TabPane tabPane;
    @FXML
    private Button addButton;
    @FXML
    private Button editButton;
    @FXML
    private Button deleteButton;
//    @FXML
//    private Button deleteButton1;

    // selectionModel is used to gather info about what the user is doing
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

        tabHandler("Customers");

    }

    @FXML
    private void appointmentsLinkHandler(ActionEvent event) {

        tabHandler("Appointments");
    }

    @FXML
    private void reportsLinkHandler(ActionEvent event) {

        tabHandler("Reports");
    }

    @FXML
    private void addButtonHandler(ActionEvent event) {
        // The current selected UI object
        Tab selectedItem = (Tab) selectionModel.getSelectedItem();
        String tabId = selectedItem.getId();
        // The Add button doesn't work on the Welcome or Reports screens
        if (!tabId.equals("welcome") && !tabId.equals("Reports")) {
            System.out.println(tabId);
            editModeHandler(event);
        }
    }

    @FXML
    private void editButtonHandler(ActionEvent event) {
        Tab selectedItem = (Tab) selectionModel.getSelectedItem();
        String tabId = selectedItem.getId();
        // The Edit button doesn't work on the Welcome or Reports screens
        if (!tabId.equals("welcome") && !tabId.equals("Reports")) {
            editModeHandler(event);
        }
    }

    @FXML
    private void deleteButtonHandler(ActionEvent event) {
        Tab selectedItem = (Tab) selectionModel.getSelectedItem();
        String tabId = selectedItem.getId();
        // The Delete button doesn't work on the Welcome or Reports screens
        if (!tabId.equals("welcome") && !tabId.equals("Reports")) {
            // Getting the scene the user is currently viewing
            Scene scene = addButton.getScene();
            // Determining which tab the user is working with to call the 
            // appropriate class
            switch (tabId) {
                // They are trying to delete a customer
                case "Customers":
                    // Getting the Customers TableView
                    TableView<Customer> custTable
                            = (TableView<Customer>) scene.lookup("#custTable");
                    // Getting the selected Customer record
                    Customer cust = custTable.getSelectionModel().getSelectedItem();
                    // Deleting that customer record from the DB
                    cust.deleteCustomerRecord();
                    // Removing that customer record from the TableView's data source
                    custTable.getItems().remove(cust);
                    // Updating the TableView
                    custTable.refresh();
                    // Selecting the first customer
                    Customer firstCust = custTable.getItems().get(0);
                    custTable.getSelectionModel().select(firstCust);
                    // Scrolling the view to the first customer
                    custTable.scrollTo(0);
                    break;
                // They are trying to delete an appointment
                case "Appointments":
                    // Getting the Appointments TableView
                    TableView<Appointment> appTable
                            = (TableView<Appointment>) scene.lookup("#appTable");
                    // Getting the current selected appointment
                    Appointment app = appTable.getSelectionModel().
                            getSelectedItem();
                    // Deleting the appointment from the DB
                    app.deleteAppointmentRecord();
                    // Getting the data source for the TableView
                    ObservableList appList = DataHandler.getAppointments();
                    // Removing the appointment from the data source
                    appList.remove(app);
                    // Selecting the first appointment
                    appTable.getSelectionModel().selectFirst();
                    // Scrolling the view to the first appointment
                    appTable.scrollTo(0);
            }
        }    
    }

    @FXML
    private void closeButtonHandler(ActionEvent event) {
        // Getting the selected Tab
        Tab selectedItem = (Tab) selectionModel.getSelectedItem();
        // If we aren't in edit mode
        if (!ApplicationState.getEditMode()) {
            // close the tab
            tabPane.getTabs().remove(selectedItem);
        }
    }
    
    // This method handles the creation of new tabs and also detects if
    // the tab already exists. If it does, instead of creating a duplicate
    // it will select the already created tab.
    private void tabHandler(String tabName) {
        // Assuming the tab doesn't exist
        boolean doesTabExist = false;
        // Getting the list of tabs
        ObservableList<Tab> tabList = tabPane.getTabs();
        Tab tab;
        // Iterating through all the current tabs
        for (Tab i : tabList) {
            // Identifying the tab by ID
            String tabId = i.getId();
            // Checking if it's null first to avoid NullPointerException
            if (tabId != null && tabId.equals(tabName)) {
                // The tab exists
                doesTabExist = true;
                tab = i;
                // So select the tab
                selectionModel.select(tab);
                // stop iterating as a match has been found
                break;
            }
        }
        // If the tab doesn't exist
        if (!doesTabExist) {
            // Make a new tab
            tab = new Tab(tabName);
            tab.setId(tabName);
            FXMLLoader loader = new FXMLLoader();
            try {
                // Loading the FXML content for the selected menu option
                AnchorPane root = loader.load(getClass().getResource(
                        Scheduler.BASE_FOLDER_PATH + tabName + ".fxml"));
                tab.setContent(root);
            } catch (IOException ioEX) {
                System.out.println("Issue loading "
                        + Scheduler.BASE_FOLDER_PATH + tabName + ".fxml");
                ioEX.printStackTrace();
            }
            // Add the new tab to the pane
            tabPane.getTabs().add(tab);
            // Select the newly created tab
            selectionModel.select(tab);
        }
    }
 
    /*
        This method enables disabled form elements and disables enabled ones.
        The exceptions are the Labels as they should always be enabled and 
        Separators as they don't have a disabled state.
    
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
        // This Lambda iterates through each child element
        children.forEach((child) -> {
            // If the child is disabled and not a Separator
            if (child.isDisabled() && !(child instanceof Separator)) {
                // Enabling all disabled children except the idField and the
                // customers phone number on the appointment screen
                if (!(child.getId().equals("idField"))
                        && !child.getId().equals("appCustPhoneField")) {
                    child.setDisable(false);
                }
            } 
            // I don't want to disable Labels and Separators don't have
            // a disabled value.
            else if (!(child instanceof Label) && !(child instanceof Separator)) {
                // Disabling Table Views
                child.setDisable(true);
            }
        });
    }
    /*
        EditModeHandler is possibly one of the most important methods in the
        application as it handles the actions the user is trying to perform
        and ensures the application is within the bounds of the application state.
        This method also handles the transformation of the buttons as the
        Add, Edit, and Delete buttons are all altered depending on whether
        we are in Edit Mode or not. 
            Edit Mode = True -> 
                Add Button becomes Save Button
                Edit Button becomes Cancel Button
                Delete button is disabled
            Edit Mode = False ->
                If transitioning from Edit Mode ->
                    Save Button becomes Add Button
                    Cancel Button becomes Edit Button
                    Delete button is enabled
            Each button has a different operation depending on its current
            state which is handled by the Switch statement embedded in the
            method.
    */
    public void editModeHandler(ActionEvent event) {
        // The current selected UI object
        Object selectedItem = selectionModel.getSelectedItem();
        // Selecting the content in the tab, which is contained in an AnchorPane
        AnchorPane pageContent = (AnchorPane) ((Tab) selectedItem).getContent();
        // Collecting the individual elements on the page
        ObservableList<Node> children = pageContent.getChildren();
        
        // Ensuring a button was pressed
        if (event.getSource() instanceof Button) {
            // Which button fired off the event?
            Button buttonPressed = (Button) event.getSource();
            String buttonId = buttonPressed.getId();
            // Determining which button we are dealing with
            switch (buttonId) {
                /* Add Button will clear form data to prepare form for a new
                objects info, put the application in edit mode, and tell the
                application that the user is trying to add something new.
                */
                case "addButton":
                    // Clear form data
                    clearFormData(children);
                    // Enable edit mode
                    ApplicationState.setEditMode(true);
                    // Telling the application that the user is trying to add
                    ApplicationState.setCurrentOperation("Add");
                    // See comments for this method
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
                
                /* Edit Button will put the appplication in edit mode and tell
                    the application that the user is trying to update something
                */
                case "editButton":
                    // Enable edit mode
                    ApplicationState.setEditMode(true);
                    // Telling the application that the user is trying to update
                    ApplicationState.setCurrentOperation("Update");
                    // See comments for this method
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
                /*
                    The Save Button is very involved. It must consider two
                    things:
                        1. What operation is the user trying to perform?
                            - Add
                            - Update
                        2. What object is the user trying to perform that
                           operation on?
                            - Appointment
                            - Customer
                    Since either operation can be performed on either object, 
                    all must be accounted for. The combination determines the 
                    action: (Object:Operation)
                        - Customer:Add = Insert new customer record, create
                            new Customer object, add to TableView.
                        - Customer:Update = Update current DB record, update
                            objet fields to new values.
                        - Appointment:Add = Insert new appointment record, 
                            create bew Appointment object, add to TableView.
                        - Appointment:Update = Update current DB record, update
                            object fields to new values.
                                        
                */
                case "saveButton":
                    // Checking for blank required fields
                    StringBuilder errors = inputValidator(children);
                    // If the StringBuilder has anything in it, it is an error
                    // and the user must be notified.
                    if(errors.length() > 0) {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Blank fields detected");
                        alert.setHeaderText("Please fill in the fields listed");
                        alert.setContentText(errors.toString());
                        alert.showAndWait();
                        
                        String exMsg = "All required fields must be filled in.";
                        throw new IllegalArgumentException(exMsg);
                    }
                    // Getting the current application Operation to validate
                    String appOperation
                            = ApplicationState.getCurrentOperation();
                    // Saving exits Edit Mode
                    ApplicationState.setEditMode(false);
                    // Disabling child elements
                    setChildElementsDisabled(children);
                    // Getting the selected tab info
                    Tab selectedTab
                            = tabPane.getSelectionModel().getSelectedItem();
                    String tabId = selectedTab.getId();
                    // Adding a new customer
                    if (appOperation.equals("Add")
                            && tabId.equals("Customers")) {
                        try {
                            // Adding new customer DB record
                            Customer cust
                                    = Customer.addCustomerRecord(children);
                            // Getting the current scene
                            Scene scene = addButton.getScene();
                            // Getting the TableView
                            TableView<Customer> custTable
                                    = (TableView<Customer>) scene.lookup(
                                            "#custTable");
                            // Add the newly created Customer Object
                            custTable.getItems().add(cust);
                            // Refresh the TableView 
                            custTable.refresh();
                            // Select the newly added customer record
                            custTable.getSelectionModel().select(cust);
                        } catch (SQLException SqlEx) {
                            SqlEx.printStackTrace();
                        }
                        finally {
                            // Cleaning up the edit mode changes
                            ApplicationState.setCurrentOperation("View");
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
                    } 
                    // Updating current customer record
                    else if (appOperation.equals("Update")
                            && tabId.equals("Customers")) {
                        try {
                            // Getting the current scene
                            Scene scene = addButton.getScene();
                            // Getting the TableView
                            TableView<Customer> custTable
                                    = (TableView<Customer>) scene.lookup(
                                            "#custTable");
                            // Getting the selected customer record
                            Customer selectedCustomer
                                    = custTable.getSelectionModel().
                                            getSelectedItem();
                            // Updating the customer record
                            selectedCustomer.updateCustomerRecord(children);
                            // Refresh the TableView
                            custTable.refresh();
                        } catch (SQLException SqlEx) {
                            SqlEx.printStackTrace();
                        } finally {
                            // Clean up the Edit Mode changes
                            ApplicationState.setCurrentOperation("View");
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
                    } 
                    // Add a new appointment
                    else if (appOperation.equals("Add")
                            && tabId.equals("Appointments")) {
                        try {
                            // Adding the DB record
                            Appointment newApp
                                    = Appointment.addAppointmentRecord(children);
                            // Setting the formatted time fields
                            newApp.setEndTimeCalendarFormatted();
                            newApp.setStartTimeCalendarFormatted();
                            newApp.setStartTimeFormatted();
                            newApp.setEndTimeFormatted();
                            // Getting the scene
                            Scene scene = addButton.getScene();
                            // Getting the TableView
                            TableView<Appointment> appTable
                                    = (TableView<Appointment>) scene.lookup(
                                            "#appTable");
                            // Getting the TableView's data source
                            ObservableList appList = DataHandler.getAppointments();
                            // Add the new appointment object
                            appList.add(newApp);
                            // Select the new Appointment object
                            appTable.getSelectionModel().select(newApp);
                            // Scroll to the new Appointment object
                            appTable.scrollTo(newApp);
                            //appTable.sort();

                        } catch (SQLException SqlEx) {
                            SqlEx.printStackTrace();
                        } 
                        // Can't have a new appointment outside business hours
                        catch (BusinessHoursException BHEx) {
                            clearFormData(children);
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Business Rule Violation");
                            alert.setHeaderText("Appointment is outside "
                                    + "business hours");
                            alert.setContentText("Appointments can only be "
                                    + "scheduled between 8:00 am and 5:00 pm");
                            alert.showAndWait();
                            break;
                        }
                        // Can't have a new appointment conflict with a current one
                        catch (AppointmentOverlapException AOEx) {
                            clearFormData(children);
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Business Rule Violation");
                            alert.setHeaderText("Appointment overlaps with"
                                    + " another.");
                            alert.setContentText(AOEx.getMessage());
                            alert.showAndWait();
                            break;
                        }
                        finally {
                            // Clean up Edit Mode changes
                            ApplicationState.setCurrentOperation("View");
                            Platform.runLater(() -> {
                                addButton.setText("Add");
                                addButton.setId("addButton");
                                editButton.setText("Edit");
                                editButton.setId("editButton");
                                deleteButton.setDisable(false);
                            });
                        }
                    } 
                    // Update current appointment
                    else if (appOperation.equals("Update")
                            && tabId.equals("Appointments")) {
                        // Get the scene
                        Scene scene = addButton.getScene();
                        // Get the TableView
                        TableView<Appointment> appTable
                                = (TableView<Appointment>) scene.lookup(
                                        "#appTable");
                        // Get the current selected appointment
                        Appointment appUpdate = appTable.
                                getSelectionModel().getSelectedItem();
                        try {
                            // Update the appointment
                            appUpdate.updateAppointmentRecord(children);
                        } catch (SQLException SqlEx) {
                            SqlEx.printStackTrace();
                        } 
                        // Updated appointment can't be outside business hours
                        catch(BusinessHoursException BHEx) {
                            clearFormData(children);
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Business Rule Violation");
                            alert.setHeaderText("Appointment is outside "
                                    + "business hours");
                            alert.setContentText("Appointments can only be "
                                    + "scheduled between 8:00 am and 5:00 pm");
                            alert.showAndWait();
                            break;
                        }
                        // Updated appointment can't conflict with an existing one
                        catch (AppointmentOverlapException AOEx) {
                            clearFormData(children);
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setTitle("Business Rule Violation");
                            alert.setHeaderText("Appointment overlaps with"
                                    + " another.");
                            alert.setContentText(AOEx.getMessage());
                            alert.showAndWait();
                            break;
                        }
                        // Clean up edit mode changes
                        finally {
                            ApplicationState.setCurrentOperation("View");
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
                        appTable.sort();
                    }
                    break;
                case "cancelButton":
                    // Checking for Edit Mode to be enabled is a little 
                    // redundant as the Cancel Button only appears in Edit Mode
                    // but it's safe and logical, I feel.
                    if (ApplicationState.getEditMode()) {
                        // Warning the user that they might have changes
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
                        // They accept that they will lose all unsaved work
                        if (result.get() == yesButton) {
                            ApplicationState.setEditMode(false);
                            ApplicationState.setCurrentOperation("View");
                            setChildElementsDisabled(children);
                            // Cleaning up edit mode changes
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
                        // They don't want to lose their unsaved changes
                        else {
                            alert.close();
                        }
                    }
                    break;
            }
        }
    }
    // Clears the form of all data. Because I can't know which object type I'm
    // working with, I validate it and then call the appropriate clear method.
    public void clearFormData(ObservableList<Node> children) {
        children.forEach((child) -> {
            if (child instanceof TextField) {
                ((TextField) child).clear();
            } else if (child instanceof CheckBox) {
                ((CheckBox) child).setSelected(false);
            } else if (child instanceof DatePicker) {
                ((DatePicker) child).setValue(null);
            } else if (child instanceof ChoiceBox) {
                ((ChoiceBox) child).getSelectionModel().
                        clearSelection();
            } else if (child instanceof TextArea) {
                ((TextArea) child).setText(null);
            } else if (child instanceof ComboBox) {
                ((ComboBox) child).setValue(null);
            }
        });
    }
    // This method will check for empty required fields
    public StringBuilder inputValidator(ObservableList<Node> children) {
        StringBuilder errorText = new StringBuilder();
        
        children.forEach(child -> {
            
            String childId = child.getId();
            // Error message template
            String errorMsg = String.format("** %s cannot be blank.\n", 
                    childId);
            
            if (child instanceof TextField) {
                // Don't clear the idField
                if(!childId.equals("idField") && !childId.equals("CustAddr2")) {
                    boolean isEmpty = ((TextField) child).
                        getText().trim().isEmpty();
                    if (isEmpty) errorText.append(errorMsg);
                }
            }
            else if (child instanceof TextArea) {
                
                String text = ((TextArea) child).getText();
                // If the TextArea text is null, then it's obviously empty
                // otherwise need to check for empty strings. Also checking for
                // null first avoids a possible NullPointerException
                boolean isEmpty = text == null ? true : text.trim().isEmpty();
                if (isEmpty) errorText.append(errorMsg);
            }
            else if (child instanceof DatePicker) {
                LocalDate childDate = ((DatePicker) child).getValue();
                if(childDate == null) errorText.append(errorMsg);
            }
            else if (child instanceof ChoiceBox) {
                Object childValue = ((ChoiceBox) child).getValue();
                if(childValue == null) errorText.append(errorMsg);
            }
            else if (child instanceof ComboBox) {
                Object childValue = ((ComboBox) child).getValue();
                if(childValue == null) errorText.append(errorMsg);
            }
        });
        return errorText;
    }
}
