/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.util.StringConverter;
import scheduler.Model.Address;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.Scheduler;
import scheduler.util.ApplicationState;
import scheduler.util.SQLConnectionHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 */
public class AppointmentsController implements Initializable {

    @FXML
    private TextField idField;
//    @FXML
//    private TextField appCustNameField;
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
    private RadioButton weekRadio;
    @FXML
    private RadioButton monthRadio;
    @FXML
    private ToggleGroup calendarViewGroup;

    private static final ObservableList APPOINTMENTS
            = FXCollections.observableArrayList();

    private static DateTimeFormatter datePickerFormatter = null;

    private static final String[] APP_LOCATIONS = {"Arizona Office",
        "New York Office",
        "London Office",
        "Customer's Home"};
    private static final String[] APP_TYPES = {"Consultation", "Session"};
    private static final ObservableList TIMES
            = FXCollections.observableArrayList();
    private static final ObservableList CUSTOMERS
            = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("In Appointments Controller");
        TimeZone tz = ApplicationState.getUserTimeZone();
        Appointment.DT_CALENDAR_FORMATTER.setTimeZone(tz);
        Locale loc = ApplicationState.getLocale();
        String locName = loc.getCountry();

        if (locName.equals("US")) {
            Appointment.DT_LOCALE_FORMATTER.applyPattern(Appointment.DT_USA_FORMAT);
            datePickerFormatter = DateTimeFormatter.ofPattern(Appointment.DT_USA_FORMAT);
        } else {
            Appointment.DT_LOCALE_FORMATTER.applyPattern(Appointment.DT_EU_FORMAT);
            datePickerFormatter = DateTimeFormatter.ofPattern(Appointment.DT_EU_FORMAT);
        }

        Appointment.DT_LOCALE_FORMATTER.setTimeZone(tz);

        FilteredList<Appointment> filteredApps = new FilteredList<>(
                APPOINTMENTS);

        calendarViewGroup.selectedToggleProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    filteredApps.setPredicate(app -> {
                        WeekFields weekFields = WeekFields.of(loc);
                        String tzId = tz.getID();
                        ZoneId tzZoneId = ZoneId.of(tzId);
                        ZonedDateTime zonedStart
                                = ((Appointment) app).getStartTime().atZone(tzZoneId);
                        LocalDateTime start = zonedStart.toLocalDateTime();
                        LocalDateTime curr
                                = LocalDateTime.now(tzZoneId);
                        if (((RadioButton) newSelection).getId().equals("weekRadio")) {

                            int startWeek = start.get(weekFields.weekOfWeekBasedYear());
                            int currWeek = curr.get(weekFields.weekOfWeekBasedYear());

                            return startWeek == currWeek;
                        } else {
                            Month startMonth = start.getMonth();
                            Month currMonth = curr.getMonth();

                            return startMonth.compareTo(currMonth) == 0;
                        }
                    });
                    appTable.sort();
                    Appointment selectedApp = appTable.getSelectionModel().getSelectedItem();
                    if (selectedApp == null) {
                        appTable.getSelectionModel().selectFirst();
                    }
                });

        SortedList<Appointment> sortedApps = new SortedList<>(filteredApps);

        Comparator<Appointment> startDateComp = new Comparator<Appointment>() {

            @Override
            public int compare(Appointment app1, Appointment app2) {
                return app1.getStartTime().compareTo(app2.getStartTime());
            }
        };

        sortedApps.setComparator(startDateComp);
        sortedApps.comparatorProperty().bind(appTable.comparatorProperty());

        String allAppointmentInfo = "SELECT app.appointmentId, \n"
                + "app.customerId, \n"
                + "cust.customerName, \n"
                + "addr.phone, \n"
                + "app.userId, \n"
                + "app.title, \n"
                + "app.description, \n"
                + "app.location, \n"
                + "app.contact, \n"
                + "app.type, \n"
                + "app.start, \n"
                + "app.end\n"
                + "FROM appointment AS app\n"
                + "INNER JOIN customer AS cust\n"
                + "ON(app.customerId = cust.customerId)\n"
                + "INNER JOIN address AS addr\n"
                + "ON(cust.addressId = addr.addressId)\n"
                + "WHERE start >= NOW()\n"
                + "ORDER BY app.start;";
        SQLConnectionHandler sql = new SQLConnectionHandler();
        Connection conn = sql.getSqlConnection();

        appStartCol.setCellValueFactory(
                new PropertyValueFactory<>("startCalFmt"));
        appEndCol.setCellValueFactory(
                new PropertyValueFactory<>("endCalFmt"));
        appTitleCol.setCellValueFactory(
                new PropertyValueFactory<>("title"));
        appLocCol.setCellValueFactory(
                new PropertyValueFactory<>("location"));

        appTable.setItems(sortedApps);

        appTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        Appointment selected = newSelection;
                        fillAppointmentForm(selected);
                    }
                });

        custCombo.setConverter(new StringConverter<Customer>() {

            @Override
            public String toString(Customer cust) {
                return cust.getName();
            }

            @Override
            public Customer fromString(String str) {
                return custCombo.getItems().stream().filter(ap
                        -> ap.getName().equals(str)).findFirst().orElse(null);
            }
        });
        custCombo.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        appCustPhoneField.setText(
                                newSelection.getCustomerAddress().
                                        getPhoneNumber());
                    }
                });
        try {
            PreparedStatement allAppInfoStmnt
                    = conn.prepareStatement(allAppointmentInfo);
            ResultSet allAppInfoRslt = allAppInfoStmnt.executeQuery();

            while (allAppInfoRslt.next()) {
                int appId = allAppInfoRslt.getInt("appointmentId");
                int custId = allAppInfoRslt.getInt("customerId");
                String custName = allAppInfoRslt.getString("customerName");
                String custPhone = allAppInfoRslt.getString("phone");
                int userId = allAppInfoRslt.getInt("userId");
                String appTitle = allAppInfoRslt.getString("title");
                String appDescr = allAppInfoRslt.getString("description");
                String appLoc = allAppInfoRslt.getString("location");
                String appContact = allAppInfoRslt.getString("contact");
                String appType = allAppInfoRslt.getString("type");
                Timestamp startSqlDate = allAppInfoRslt.getTimestamp("start");
                LocalDateTime appStartDate = startSqlDate.toLocalDateTime();
                Timestamp endSqlDate = allAppInfoRslt.getTimestamp("end");
                LocalDateTime appEndDate = endSqlDate.toLocalDateTime();
                String startCalendarFmt = Appointment.DT_CALENDAR_FORMATTER.format(
                        startSqlDate);
                String startLocaleFmt = Appointment.DT_LOCALE_FORMATTER.format(startSqlDate);
                String endCalendarFmt = Appointment.DT_CALENDAR_FORMATTER.format(endSqlDate);
                String endLocaleFmt = Appointment.DT_LOCALE_FORMATTER.format(endSqlDate);

                Appointment newApp = new Appointment(appId, custId, userId,
                        appTitle, appLoc, appType,
                        appContact, appDescr, appStartDate,
                        appEndDate, startLocaleFmt,
                        endLocaleFmt, startCalendarFmt,
                        endCalendarFmt);
                APPOINTMENTS.add(newApp);
            }
        } catch (SQLException SqlEx) {
            SqlEx.printStackTrace();
        }

        //calendarViewGroup.selectToggle(weekRadio);
        appTable.getSelectionModel().selectFirst();
    }

    private void fillAppointmentForm(Appointment app) {
        Customer appCust = app.getCustomer();
        Address appCustAddr = app.getCustomerAddress();
        appLocationChoice.getItems().setAll(APP_LOCATIONS);
        appTypeChoice.getItems().setAll(APP_TYPES);

        appTitleField.setText(app.getTitle());
        idField.setText(Integer.toString(app.getId()));
//        appCustNameField.setText(appCust.getName());

        final int HOURS_IN_DAY = 24;

        for (int i = 0; i <= HOURS_IN_DAY; i++) {
            String timeString;

            if (i <= 9) {
                timeString = String.format("0%d:00", i);
            } else {
                timeString = String.format("%d:00", i);
            }
            TIMES.add(timeString);
        }

        startCombo.setItems(TIMES);
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(
                    Scheduler.BASE_FOLDER_PATH
                    + "Customers.fxml"));
            Parent root = loader.load();
            CustomersController custController
                    = loader.getController();
            ObservableList custList
                    = custController.getCustomersList();

            CUSTOMERS.addAll(custList);
        } catch (IOException IOEx) {
            IOEx.printStackTrace();
        }

        custCombo.setItems(CUSTOMERS);

        custCombo.getSelectionModel().select(appCust);

        String startTimeFormat = "HH:00";
        DateTimeFormatter startTimeFormatter
                = DateTimeFormatter.ofPattern(startTimeFormat);
        String dbTimeZoneId = ApplicationState.getUserTimeZone().getID();
        startTimeFormatter.withZone(ZoneId.of(dbTimeZoneId));
        LocalDateTime appStartTime = app.getStartTime();
        String appStartTimeFmt = startTimeFormatter.format(appStartTime);

        startCombo.setValue(appStartTimeFmt);

        appCustPhoneField.setText(appCustAddr.getPhoneNumber());
        appDatePicker.setValue(LocalDate.parse(
                app.getStartTimeFormatted(), datePickerFormatter));
        appLocationChoice.setValue(app.getLocation());
        appTypeChoice.setValue(app.getType());

        LocalTime startTime = app.getStartTime().toLocalTime();
        LocalTime endTime = app.getEndTime().toLocalTime();
        Duration duration = Duration.between(startTime, endTime);
        long durationMinutes = duration.getSeconds() / 60;

        appDurationField.setText(Long.toString(durationMinutes));
        appContactField.setText(app.getContact());
        appDescriptionArea.setText(app.getDescription());
    }

    public static ObservableList<Appointment> getAppointmentsList() {
        return APPOINTMENTS;
    }
}
