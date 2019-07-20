package scheduler.View_Controller;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import scheduler.Model.Address;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.util.ApplicationState;
import scheduler.util.DataHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 * This class controls the interactions with the Appointments.fxml UI.
 */
public class AppointmentsController implements Initializable {

    @FXML
    private TextField idField;
    @FXML
    private TextField appCustPhoneField;
    @FXML
    private DatePicker appDatePicker;
    @FXML
    private TextArea appDescriptionArea;
    @FXML
    private TextField appContactField;
    @FXML
    private ChoiceBox<String> appLocationChoice;
    @FXML
    private ChoiceBox<String> appTypeChoice;
    @FXML
    private ComboBox<String> startCombo;
    @FXML
    private ComboBox<Customer> custCombo;
    @FXML
    private TextField appTitleField;
    @FXML
    private TableView<Appointment> appTable;
    @FXML
    private TableColumn<Appointment, String> appStartCol;
    @FXML
    private TableColumn<Appointment, String> appEndCol;
    @FXML
    private TableColumn<Appointment, String> appTitleCol;
    @FXML
    private TableColumn<Appointment, String> appLocCol;
    @FXML
    private TextField appDurationField;
    @FXML
    private ToggleGroup calendarViewGroup;
    @FXML
    private RadioButton weekRadio;
    @FXML
    private RadioButton monthRadio;

    // The DateTimeFormatter to be used to format the Date Picker Control's data
    private static DateTimeFormatter datePickerFormatter = null;
    // This array represents the static options for appointment locations.
    // It is loaded intothe appLocationChoice Choicebox.
    private static final String[] APP_LOCATIONS = {"Arizona Office",
        "New York Office",
        "London Office",
        "Customer's Home"};
    // This array represents the static options for appointment types.
    // It is loaded into the appTypeChoice Choicebox.
    private static final String[] APP_TYPES = {"Consultation", "Session"};
    // This array will contain the appointment start time choices which will be
    // On the hour only.
    private static final ObservableList TIMES
            = FXCollections.observableArrayList();
    // This array will be a copy of the CUSTOMERS array in the CustomersController
//    private static final ObservableList CUSTOMERS
//            = FXCollections.observableArrayList();
    private ObservableList CUSTOMERS = null;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // The current time zone of the logged in user
        TimeZone tz = ApplicationState.getUserTimeZone();
        // Setting that time zone to the datetime formatter
        Appointment.DT_CALENDAR_FORMATTER.setTimeZone(tz);
        // Getting the locale of the user's machine
        Locale loc = ApplicationState.getLocale();
        // Getting the locale country string identifier
        String locName = loc.getCountry();
        // Getting the CUSTOMERS ObservableList
        CUSTOMERS = DataHandler.getCustomers();
        // Obtaining the ObservableList data source backing the appTable 
        // TableView.
        ObservableList apps = DataHandler.getAppointments();
        // Setting the datetime format based on locale. Currently only accounting
        // for US and non-US. All non-US locales will use the EU format.
        if (locName.equals("US")) {
            Appointment.DT_LOCALE_FORMATTER.applyPattern(Appointment.DT_USA_FORMAT);
            datePickerFormatter = DateTimeFormatter.ofPattern(Appointment.DT_USA_FORMAT);
        } else {
            Appointment.DT_LOCALE_FORMATTER.applyPattern(Appointment.DT_EU_FORMAT);
            datePickerFormatter = DateTimeFormatter.ofPattern(Appointment.DT_EU_FORMAT);
        }
        // Setting the user's time zone to the locale formatter.
        Appointment.DT_LOCALE_FORMATTER.setTimeZone(tz);
        // This Filtered List is used to fulfill requirement D.
        FilteredList<Appointment> filteredApps = new FilteredList<>(
                apps);
        // This this Lambda applies a listener which is a vehicle to kick off 
        // the implementation to fulfill requirement D.
        calendarViewGroup.selectedToggleProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    // Setting the predicate that implements requirement D
                    // on the FilteredList created above.
                    filteredApps.setPredicate(app -> {
                        // This is a static attribute that represents the week
                        // fields for a specific locale. Some locales may
                        // start their week on a Sunday while others on Monday.
                        WeekFields weekFields = WeekFields.of(loc);
                        // Getting the Time Zone ID which is used to enforce
                        // the time zone on some date time objects.
                        String tzId = tz.getID();
                        ZoneId tzZoneId = ZoneId.of(tzId);
                        //  Ensuring the appointment's start time is of the
                        // correct time zone.
                        ZonedDateTime zonedStart
                                = ((Appointment) app).getStartTime().atZone(tzZoneId);
                        LocalDateTime start = zonedStart.toLocalDateTime();
                        LocalDateTime curr
                                = LocalDateTime.now(tzZoneId);
                        // Identifying which RadioButton in the ChoiceGroup
                        // was selected to determine the filtering.
                        if (((RadioButton) newSelection).getId().equals("weekRadio")) {
                            // Filter appointments by week
                            int startWeek = start.get(weekFields.weekOfWeekBasedYear());
                            int currWeek = curr.get(weekFields.weekOfWeekBasedYear());
                            // Returns the boolean result of the date comparison
                            return startWeek == currWeek;
                        } else {
                            // The only other option is by Month
                            Month startMonth = start.getMonth();
                            Month currMonth = curr.getMonth();
                            // Returns the boolean result of the date comparison
                            return startMonth.compareTo(currMonth) == 0;
                        }
                    });
                    // Sort the view when the predicate is called.
                    appTable.sort();
                    // Getting the currently selected appointment
                    Appointment selectedApp = appTable.getSelectionModel().getSelectedItem();
                    // Null means nothing is selected currently
                    if (selectedApp == null) {
                        // So select the first thing
                        appTable.getSelectionModel().selectFirst();
                    }
                });
        // Binding the FilteredList to a SortedList which is later bound
        // to the TableView
        SortedList<Appointment> sortedApps = new SortedList<>(filteredApps);
        // This Comparator represents sorting logic
        Comparator<Appointment> startDateComp = new Comparator<Appointment>() {

            @Override
            public int compare(Appointment app1, Appointment app2) {
                return app1.getStartTime().compareTo(app2.getStartTime());
            }
        };
        // Applying the above Comparator
        sortedApps.setComparator(startDateComp);
        // Binding the ComparatorProperty to the appTable's ComparatorProperty
        sortedApps.comparatorProperty().bind(appTable.comparatorProperty());
        
        // Configuring the Cell Value Factories by setting a PropertyValueFactory
        // which utilizes the Java Beans Property configured in the
        // Appointment model.
        appStartCol.setCellValueFactory(
                new PropertyValueFactory<>("startCalFmt"));
        appEndCol.setCellValueFactory(
                new PropertyValueFactory<>("endCalFmt"));
        appTitleCol.setCellValueFactory(
                new PropertyValueFactory<>("title"));
        appLocCol.setCellValueFactory(
                new PropertyValueFactory<>("location"));
        // Binding the Filtered and then Sorted List to the TableView
        appTable.setItems(sortedApps);
        // Setting a listener to detect when the TableView selection is changed
        // by the user so the new objects data will be displayed.
        appTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        Appointment selected = newSelection;
                        fillAppointmentForm(selected);
                    }
                });
        // This StringConverter will allow the custCombo Combo Box the ability
        // to show the customer's name instead of the object memory reference.
        custCombo.setConverter(new StringConverter<Customer>() {
            // Display the name
            @Override
            public String toString(Customer cust) {
                return cust.getName();
            }
            // Get the actual object so its methods and fields can be utilized.
            @Override
            public Customer fromString(String str) {
                return custCombo.getItems().stream().filter(ap
                        -> ap.getName().equals(str)).findFirst().orElse(null);
            }
        });
        // Adding a listener to the Customer Combo Box so when the selection
        // is changed by the user, the customer's telephone number is updated
        custCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        appCustPhoneField.setText(
                                newSelection.getCustomerAddress().
                                        getPhoneNumber());
                    }
                });
        // Setting the data source for the Customers Combo Box
        custCombo.setItems(CUSTOMERS);
        // Setting the available locations
        appLocationChoice.getItems().setAll(APP_LOCATIONS);
        // Setting the available appointment types
        appTypeChoice.getItems().setAll(APP_TYPES);
        // Represents the 24 hours in a day
        final int HOURS_IN_DAY = 24;
        // Looping for every hour in the day to format it to a string
        for (int i = 0; i <= HOURS_IN_DAY; i++) {
            String timeString;
            // Adding a zero to single digits
            if (i <= 9) {
                timeString = String.format("0%d:00", i);
            } else {
                timeString = String.format("%d:00", i);
            }
            // Adding the formatted timeString to the TIMES array
            TIMES.add(timeString);
        }
        // Setting the backing data souce for the Start Time Combo Box
        startCombo.setItems(TIMES);
        // Select the radio button to filter by week
        calendarViewGroup.selectToggle(monthRadio);
        // Select the first item
        appTable.getSelectionModel().selectFirst();
    }
    
    // This method is used to populate the appointment form data with the
    // Appointment object's field information.
    private void fillAppointmentForm(Appointment app) {
        // Some customer info is needed
        Customer appCust = app.getCustomer();
        // Some Customer Address info is needed
        Address appCustAddr = app.getCustomerAddress();
        
        
        appTitleField.setText(app.getTitle());
        idField.setText(Integer.toString(app.getId()));
        // Selecting the Customer assigned to this appointment.
        custCombo.getSelectionModel().select(appCust);
        // String formatter for the Start Time Combo Box
        String startTimeFormat = "HH:00";
        // Instantiating the DateTimeFormatter and setting the format pattern
        DateTimeFormatter startTimeFormatter
                = DateTimeFormatter.ofPattern(startTimeFormat);
        // Getting Database time zone info
        String dbTimeZoneId = ApplicationState.getUserTimeZone().getID();
        // Applying the DB time zone info to the formatter
        startTimeFormatter.withZone(ZoneId.of(dbTimeZoneId));
        // The current start time of the appointment
        LocalDateTime appStartTime = app.getStartTime();
        // Formatting the appointment's start time
        String appStartTimeFmt = startTimeFormatter.format(appStartTime);
        // Setting the formatted start time.
        startCombo.setValue(appStartTimeFmt);
        
        appCustPhoneField.setText(appCustAddr.getPhoneNumber());
        appDatePicker.setValue(app.getStartTime().toLocalDate());
        appLocationChoice.setValue(app.getLocation());
        appTypeChoice.setValue(app.getType());
        // Getting set up to to determine the duration of the appointment
        LocalTime startTime = app.getStartTime().toLocalTime();
        LocalTime endTime = app.getEndTime().toLocalTime();
        // This Duration represents the difference between the start and end
        Duration duration = Duration.between(startTime, endTime);
        // Converting the seconds to minutes
        long durationMinutes = duration.getSeconds() / 60;
        // Displaying the duration in minutes
        appDurationField.setText(Long.toString(durationMinutes));
        appContactField.setText(app.getContact());
        appDescriptionArea.setText(app.getDescription());
    }
}
