package scheduler.Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import scheduler.util.ApplicationState;
import scheduler.util.DataHandler;
import scheduler.util.SQLConnectionHandler;

/**
 *
 * @author Cory
 * This class represents a customer appointment in the application and is 
 * modeled after the appointment database table. This class is a bit complex as
 * it handles Date/Time locale formatting and time zones.
 */
public class Appointment {

    // SQL DB time zone
    private static final TimeZone SQL_TZ
            = ApplicationState.getDatabaseTimeZone();
    // Used for time zone conversions to match the DB time
    private static final String SQL_TZ_ID = SQL_TZ.getID();
    // The date format string for the appointment calendar in the app
    private static final String DT_CALENDAR_FORMAT
            = "EEE, MMM dd, yyyy " + "hh:mm:ss";
    // The date/time format string for EU and other parts of the world
    public static final String DT_EU_FORMAT = "dd/MM/yyyy HH:mm:ss";
    // The date/time format string for the US
    public static final String DT_USA_FORMAT = "MM/dd/yyyy HH:mm:ss";
    // The date/time format string for the database
    private static final String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    // BUSINESS_START and BUSINESS_END represent the business hours
    private static final LocalTime BUSINESS_START = LocalTime.parse("08:00");
    private static final LocalTime BUSINESS_END = LocalTime.parse("17:00");
    
    // Setting up the date/time formatters
    private static final DateTimeFormatter SQL_DATE_FORMATTER
            = DateTimeFormatter.ofPattern(SQL_DATE_FORMAT).withZone(
                    ZoneId.of(SQL_TZ_ID));
    public static final SimpleDateFormat DT_LOCALE_FORMATTER = new SimpleDateFormat();
    public static final SimpleDateFormat DT_CALENDAR_FORMATTER
            = new SimpleDateFormat(DT_CALENDAR_FORMAT);
    
    // Utilizing Java Beans Properties rather than standard primitives in order
    // to easily show the field information in the application.
    private final IntegerProperty id;
    private final IntegerProperty custId;
    private final IntegerProperty userId;
    private final StringProperty title;
    private final StringProperty location;
    private final StringProperty type;
    private final StringProperty contact;
    private final StringProperty description;
    private final ObjectProperty<LocalDateTime> startTime;
    private final StringProperty startTimeFmt;
    private final StringProperty startCalFmt;
    private final ObjectProperty<LocalDateTime> endTime;
    private final StringProperty endTimeFmt;
    private final StringProperty endCalFmt;
    
    // The customer the appointment is for and their address info
    private Customer customer;
    private Address customerAddress;
    
    // A constructor that doesn't have values for the formatted date strings
    public Appointment(int id, int custId, int userId, String title,
            String location, String type, String contact, String desc,
            LocalDateTime start, LocalDateTime end) {
        this.id = new SimpleIntegerProperty(id);
        this.custId = new SimpleIntegerProperty(custId);
        this.userId = new SimpleIntegerProperty(userId);
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.type = new SimpleStringProperty(type);
        this.contact = new SimpleStringProperty(contact);
        this.description = new SimpleStringProperty(desc);
        this.startTime = new SimpleObjectProperty<>(start);
        this.startTimeFmt = new SimpleStringProperty(null);
        this.startCalFmt = new SimpleStringProperty(null);
        this.endTime = new SimpleObjectProperty<>(end);
        this.endTimeFmt = new SimpleStringProperty(null);
        this.endCalFmt = new SimpleStringProperty(null);
        this.customer = null;
        this.customerAddress = null;
    }
    
    // A constructor that does have values for the formatted date strings
    public Appointment(int id, int custId, int userId, String title,
            String location, String type, String contact, String desc,
            LocalDateTime start, LocalDateTime end, String startFmt,
            String endFmt, String sCalFmt, String eCalFmt) {
        this.id = new SimpleIntegerProperty(id);
        this.custId = new SimpleIntegerProperty(custId);
        this.userId = new SimpleIntegerProperty(userId);
        this.title = new SimpleStringProperty(title);
        this.location = new SimpleStringProperty(location);
        this.type = new SimpleStringProperty(type);
        this.contact = new SimpleStringProperty(contact);
        this.description = new SimpleStringProperty(desc);
        this.startTime = new SimpleObjectProperty<>(start);
        this.startTimeFmt = new SimpleStringProperty(startFmt);
        this.startCalFmt = new SimpleStringProperty(sCalFmt);
        this.endTime = new SimpleObjectProperty<>(end);
        this.endTimeFmt = new SimpleStringProperty(endFmt);
        this.endCalFmt = new SimpleStringProperty(eCalFmt);
        this.customer = null;
        this.customerAddress = null;
    }

    public int getId() {
        return this.id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public IntegerProperty idProperty() {
        return this.id;
    }

    public int getCustId() {
        return this.custId.get();
    }

    public void setCustId(int id) {
        this.custId.set(id);
    }

    public IntegerProperty custIdProperty() {
        return this.custId;
    }

    public int getUserId() {
        return this.userId.get();
    }

    public void setUserId(int id) {
        this.userId.set(id);
    }

    public IntegerProperty userIdProperty() {
        return this.userId;
    }

    public String getTitle() {
        return this.title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public StringProperty titleProperty() {
        return this.title;
    }

    public String getLocation() {
        return this.location.get();
    }

    public void setLocation(String loc) {
        this.location.set(loc);
    }

    public StringProperty locationProperty() {
        return this.location;
    }

    public String getType() {
        return this.type.get();
    }

    public void setType(String type) {
        this.type.set(type);
    }

    public StringProperty typeProperty() {
        return this.type;
    }

    public String getContact() {
        return this.contact.get();
    }

    public void setContact(String contact) {
        this.contact.set(contact);
    }

    public StringProperty contactProperty() {
        return this.contact;
    }

    public String getDescription() {
        return this.description.get();
    }

    public void setDescription(String desc) {
        this.description.set(desc);
    }

    public StringProperty descriptionProperty() {
        return this.description;
    }

    public LocalDateTime getStartTime() {
        return this.startTime.get();
    }

    public void setStartTime(LocalDateTime start) {
        this.startTime.set(start);
    }

    public ObjectProperty<LocalDateTime> startTimeProperty() {
        return this.startTime;
    }
    
    // This getter checks if the formatted start time string is set, otherwise
    // it calls the setter and then returns the set value.
    public String getStartTimeFormatted() {
        
        String startFmt = this.startTimeFmt.get();

        if (startFmt == null) {
            this.setStartTimeFormatted();
            startFmt = this.startTimeFmt.get();
        }

        return startFmt;
    }

    public void setStartTimeFormatted() {
        LocalDateTime appStartTime = this.getStartTime();
        // Converting to timestamp to pass into the formatter
        Timestamp timestamp = Timestamp.valueOf(appStartTime);
        // Formatting the datetime according to locale
        String startFmt = DT_LOCALE_FORMATTER.format(timestamp);
        this.startTimeFmt.set(startFmt);
    }

    public StringProperty startTimeFmtProperty() {
        return this.startTimeFmt;
    }
    
    // This getter checks if the formatted start time string is set, otherwise
    // it calls the setter and then returns the set value.
    public String getStartTimeCalendarFormatted() {
        String startFmt = this.startCalFmt.get();

        if (startFmt == null) {
            this.setStartTimeCalendarFormatted();
            startFmt = this.startCalFmt.get();
        }

        return startFmt;
    }

    public void setStartTimeCalendarFormatted() {
        LocalDateTime starTime = this.getStartTime();
        // Converting to timestamp to pass into the formatter
        Timestamp timestamp = Timestamp.valueOf(starTime);
        // Formatting the datetime according to calendar
        String startFmt = DT_CALENDAR_FORMATTER.format(timestamp);
        this.startCalFmt.set(startFmt);
    }

    public StringProperty startCalFmtProperty() {
        return this.startCalFmt;
    }

    public LocalDateTime getEndTime() {
        return this.endTime.get();
    }

    public void setEndTime(LocalDateTime end) {
        this.endTime.set(end);
    }

    public ObjectProperty<LocalDateTime> endTimeProperty() {
        return this.endTime;
    }
    
    // This getter checks if the formatted start time string is set, otherwise
    // it calls the setter and then returns the set value.
    public String getEndTimeFormatted() {
        String endFmt = this.endTimeFmt.get();

        if (endFmt == null) {
            this.setEndTimeFormatted();
            endFmt = this.endTimeFmt.get();
        }

        return endFmt;
    }

    public void setEndTimeFormatted() {
        LocalDateTime end = this.getEndTime();
        // Converting to timestamp to pass into the formatter
        Timestamp timestamp = Timestamp.valueOf(end);
        // Formatting the datetime according to locale
        String endFmt = DT_LOCALE_FORMATTER.format(timestamp);
        this.endTimeFmt.set(endFmt);
    }

    public StringProperty endTimeFmtProperty() {
        return this.endTimeFmt;
    }
    
    // This getter checks if the formatted start time string is set, otherwise
    // it calls the setter and then returns the set value.
    public String getEndTimeCalendarFormatted() {
        String endFmt = this.endCalFmt.get();

        if (endFmt == null) {
            this.setEndTimeCalendarFormatted();
            endFmt = this.endCalFmt.get();
        }

        return endFmt;
    }

    public void setEndTimeCalendarFormatted() {
        LocalDateTime endTime = this.getEndTime();
        // Converting to timestamp to pass into the formatter
        Timestamp timestamp = Timestamp.valueOf(endTime);
        // Formatting the datetime according to calendar
        String endFmt = DT_CALENDAR_FORMATTER.format(timestamp);
        this.endCalFmt.set(endFmt);
    }

    public StringProperty endCalFmtProperty() {
        return this.endCalFmt;
    }
    
    // This getter will return the Customer object associated with this
    // appointment if it's not null, otherwise it will query the database
    // For the customer info, create a new customer object and assign it.
    public Customer getCustomer() {
        Customer cust = null;

        if (this.customer == null) {
            int custObjId = this.getCustId();
            SQLConnectionHandler sql = new SQLConnectionHandler();
            Connection conn = sql.getSqlConnection();
            String query = "SELECT * FROM customer WHERE customerId = ?";
            try {
                PreparedStatement pstmnt = conn.prepareStatement(query);
                pstmnt.setInt(1, custObjId);
                ResultSet result = pstmnt.executeQuery();
                if (result.next()) {
                    String custName = result.getString("customerName");
                    int active = result.getInt("active");
                    int addressId = result.getInt("addressId");
                    cust = new Customer(custObjId, custName, active, addressId);
                    this.customer = cust;
                }
            } catch (SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
        } else {
            cust = this.customer;
        }

        return cust;
    }

    public void setCustomer(Customer cust) {
        if (cust != null) {
            this.customer = cust;
        }
    }
    
    // This getter checks if the customerAddress field is null. If it is and
    // the customer object isn't null, it will call the getCustomerAddress
    // method of the customer which returns an object to assign.
    public Address getCustomerAddress() {
        Address addr = null;
        // If there's no address but there is a customer
        if (this.customerAddress == null && this.customer != null) {
            addr = this.customer.getCustomerAddress();
        } else {
            addr = this.customerAddress;
        }
        return addr;
    }
    
    // This method takes in an ObservableList of nodes from the UI form which
    // contain the data to make an appointment, creates the appointment in the
    // database, creates a new appointment object, and adds the object
    // to the ObservableList bound to the TableView.
    public static Appointment addAppointmentRecord(
            ObservableList<Node> children) throws IllegalStateException,
            SQLException, BusinessHoursException, 
            AppointmentOverlapException {
        // The new appointment to be returned
        Appointment app = null;
        // Used to ensure the application is in an Add state
        String appOperation = ApplicationState.getCurrentOperation();
        // What User is currently logged into the application?
        String currUser = ApplicationState.getCurrentUser();
        
        // The app must be in the Add mode
        if (appOperation.equals("Add")) {
            SQLConnectionHandler sql = new SQLConnectionHandler();
            Connection conn = sql.getSqlConnection();
            // SQL string used to make the insert prepared statement
            String insertApp = "INSERT INTO appointment"
                    + "(customerId, userId, title, description, location,"
                    + "contact, type, start, end, createDate, createdBy,"
                    + "lastUpdateBy, url)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?, ?, \"\");";
            // Statement used to execute SQL command that will return the
            // auto generated ID primary key
            PreparedStatement pstmnt = conn.prepareStatement(insertApp,
                    Statement.RETURN_GENERATED_KEYS);
            
            // Appointment fields
            int appId = 0;
            int custId = 0;
            int userId = 0;
            long duration = 0;
            String appTitle = null;
            String appLoc = null;
            String appType = null;
            String appContact = null;
            String appDesc = null;
            LocalDateTime appStart = null;
            String appStartFmt = null;
            String appStartCalFmt = null;
            LocalDateTime appEnd = null;
            String appEndFmt = null;
            String appEndCalFmt = null;
            LocalDate dpDate = null;
            LocalTime startComboTime = null;
            
            // Begin grabbing form data
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
                // Since I can't know what type of object I'm working with
                // and each object has different methods for getting the form
                // data, I'm validating the object type, casting it to the child
                // and calling the appropriate method.
                if (child instanceof TextField) {

                    String childText = ((TextField) child).getText();

                    switch (childId) {

                        case "appTitleField":
                            appTitle = childText;
                            break;
                        case "appDurationField":
                            duration = Long.parseLong(childText);
                            break;
                        case "appContactField":
                            appContact = childText;
                            break;
                    }
                }

                else if (child instanceof TextArea) {

                    String childText = ((TextArea) child).getText();
                    appDesc = childText;
                }

                else if (child instanceof DatePicker) {

                    LocalDate childDate = ((DatePicker) child).getValue();
                    dpDate = childDate;
                }

                else if (child instanceof ChoiceBox) {

                    String childText = ((ChoiceBox<String>) child).getValue();

                    switch (childId) {

                        case "appTypeChoice":
                            appType = childText;
                            break;
                        case "appLocationChoice":
                            appLoc = childText;
                            break;
                    }
                }

                else if (child instanceof ComboBox) {

                    switch (childId) {

                        case "startCombo":
                            String comboValue
                                    = ((ComboBox<String>) child).getValue();
                            startComboTime = LocalTime.parse(comboValue);
                            break;
                        case "custCombo":
                            Customer cust
                                    = ((ComboBox<Customer>) child).getValue();
                            custId = cust.getId();
                            break;
                    }
                }
            }

            userId = ApplicationState.getCurrUserId();
            
            // Validating that the appointment is within business hours
            boolean isBeforeStart = startComboTime.isBefore(BUSINESS_START);
            boolean isAfterEnd = startComboTime.
                    plusMinutes(duration).isAfter(BUSINESS_END);
            
            if (isBeforeStart || isAfterEnd) {
               throw new BusinessHoursException("Appointment is outside"
                       + "business hours."); 
            }
            
            // Getting the ObservableList bound to the TableView
            ObservableList appList = DataHandler.getAppointments();
            
            // Iterating through current appointments to validate that the new
            // appointment doesn't conflict with an existing one.
            for(int i = 0; i < appList.size(); i++) {
                Appointment apmnt = (Appointment) appList.get(i);
                LocalDate appDate = apmnt.getStartTime().toLocalDate();
                LocalTime appStartTime = apmnt.getStartTime().toLocalTime();
                LocalTime appEndTime = apmnt.getEndTime().toLocalTime();
                
                // Conflict condition
                if( dpDate.equals(appDate) && 
                        (startComboTime.equals(appStartTime) || 
                        startComboTime.plusMinutes(duration).equals(appEndTime))
                   ) {
                    String errMsg = "This appointment overlaps with another."
                            + " Please reschedule to resolve the conflict.";
                    throw new AppointmentOverlapException(errMsg);
                }
            }
            
            // Setting the prepared statement info
            pstmnt.setInt(1, custId);
            pstmnt.setInt(2, userId);
            pstmnt.setString(3, appTitle);
            pstmnt.setString(4, appDesc);
            pstmnt.setString(5, appLoc);
            pstmnt.setString(6, appContact);
            pstmnt.setString(7, appType);
            
            // Formatting datetime objects to the SQL time zone and format
            String LocalDateTimeFmt = dpDate.atTime(startComboTime).
                    format(SQL_DATE_FORMATTER);
            LocalDateTime sqlStart = LocalDateTime.parse(LocalDateTimeFmt, SQL_DATE_FORMATTER);

            LocalDateTime sqlEnd = sqlStart.plusMinutes(duration);

            pstmnt.setObject(8, sqlStart);
            pstmnt.setObject(9, sqlEnd);
            pstmnt.setString(10, ApplicationState.getCurrentUser());
            pstmnt.setString(11, currUser);
            
            try {
                // Executing prepared statement
                int rowsAffected = pstmnt.executeUpdate();

                if (rowsAffected == 1) {
                    ResultSet key = pstmnt.getGeneratedKeys();
                    if (key.next()) {
                        // Getting the auto generated key
                        appId = key.getInt(1);
                    }
                }
            } catch (SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err = "There was an error adding the Appointment Record"
                        + "to the database.";
                SQLException ex = new SQLException(err);
                throw ex;
            }
            
            // Creating the new appointment object
            app = new Appointment(appId, custId, userId, appTitle, appLoc,
                    appType, appContact, appDesc, sqlStart, sqlEnd);

        } else {
            String err = String.format("ApplicationState does not permit adding a new "
                    + "Appointment. \nApp Operation = %s\nEdit Mode = %s1",
                    ApplicationState.getCurrentOperation(),
                    ApplicationState.getEditMode());
            IllegalStateException ex = new IllegalStateException(err);
            throw ex;
        }

        return app;
    }
    
    // This method takes in an ObservableList of nodes from the UI form which
    // contain the data to update the existing appointment. This doesn't take
    // in the appointment to update as it just operates off of the appointment
    // from which it is called.
    public void updateAppointmentRecord(ObservableList<Node> children)
            throws IllegalStateException, SQLException , 
            BusinessHoursException, AppointmentOverlapException {
        
        // Getting the current application operation to validate 
        String appOperation = ApplicationState.getCurrentOperation();
        // Get current logged in user
        String currUser = ApplicationState.getCurrentUser();
        
        // Validating that the app is in Update mode
        if (appOperation.equals("Update")) {
            // The SQL update string to be used for the prepared statement
            String updateApp = "UPDATE appointment SET customerId = ?, "
                    + "title = ?, description = ?, location = ?, contact = ?,"
                    + "type = ?, start = ?, end = ?, lastUpdate = NOW(),"
                    + "lastUpdateBy = ? WHERE appointmentId = ?";
            SQLConnectionHandler sql = new SQLConnectionHandler();
            Connection conn = sql.getSqlConnection();
            // The statement to configure and execute
            PreparedStatement pstmnt = conn.prepareCall(updateApp);
            
            // The appointment fields
            int appId = 0;
            int custId = 0;
            int userId = 0;
            long duration = 0;
            String appTitle = null;
            String appLoc = null;
            String appType = null;
            String appContact = null;
            String appDesc = null;
            LocalDateTime appStart = null;
            String appStartFmt = null;
            String appStartCalFmt = null;
            LocalDateTime appEnd = null;
            String appEndFmt = null;
            String appEndCalFmt = null;
            LocalDate dpDate = null;
            LocalTime startComboTime = null;
            // Getting the node data
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();
                
                // Since I can't know what type of object I'm working with
                // and each object has different methods for getting the form
                // data, I'm validating the object type, casting it to the child
                // and calling the appropriate method.
                if (child instanceof TextField) {

                    String childText = ((TextField) child).getText();

                    switch (childId) {

                        case "idField":
                            appId = Integer.parseInt(childText);
                            break;
                        case "appTitleField":
                            this.setTitle(childText);
                            break;
                        case "appDurationField":
                            duration = Long.parseLong(childText);
                            break;
                        case "appContactField":
                            this.setContact(childText);
                            break;
                    }
                }

                if (child instanceof TextArea) {

                    String childText = ((TextArea) child).getText();
                    this.setDescription(childText);
                }

                if (child instanceof DatePicker) {

                    LocalDate childDate = ((DatePicker) child).getValue();
                    dpDate = childDate;
                }

                if (child instanceof ChoiceBox) {

                    String childText = ((ChoiceBox<String>) child).getValue();

                    switch (childId) {

                        case "appTypeChoice":
                            this.setType(childText);
                            break;
                        case "appLocationChoice":
                            this.setLocation(childText);
                            break;
                    }
                }

                if (child instanceof ComboBox) {

                    switch (childId) {

                        case "startCombo":
                            String comboValue
                                    = ((ComboBox<String>) child).getValue();
                            startComboTime = LocalTime.parse(comboValue);
                            break;
                        case "custCombo":
                            Customer cust
                                    = ((ComboBox<Customer>) child).getValue();
                            this.setCustId(cust.getId());
                            break;
                    }
                }
            }
            
            // Validating that the updated time isn't outside business hours.
            boolean isBeforeStart = startComboTime.isBefore(BUSINESS_START);
            boolean isAfterEnd = startComboTime.
                    plusMinutes(duration).isAfter(BUSINESS_END);
            
            if (isBeforeStart || isAfterEnd) {
               throw new BusinessHoursException("Appointment is outside"
                       + "business hours."); 
            }
            
            // Current appointments
            ObservableList appList = DataHandler.getAppointments();
            
            // Validating the new updated appointment doesn't conflict with
            // an existing appointment
            for(int i = 0; i < appList.size(); i++) {
                Appointment apmnt = (Appointment) appList.get(i);
                LocalDate appDate = apmnt.getStartTime().toLocalDate();
                LocalTime appStartTime = apmnt.getStartTime().toLocalTime();
                LocalTime appEndTime = apmnt.getEndTime().toLocalTime();
                
                // Conflict condition
                if( dpDate.equals(appDate) && 
                        (startComboTime.equals(appStartTime) || 
                        startComboTime.plusMinutes(duration).equals(appEndTime))
                   ) {
                    String errMsg = "This appointment overlaps with another."
                            + " Please reschedule to resolve the conflict.";
                    throw new AppointmentOverlapException(errMsg);
                }
            }
            
            // Formatting time to SQL time zone and format
            String LocalDateTimeFmt = dpDate.atTime(startComboTime).
                    format(SQL_DATE_FORMATTER);
            LocalDateTime sqlStart
                    = LocalDateTime.parse(LocalDateTimeFmt, SQL_DATE_FORMATTER);

            LocalDateTime sqlEnd = sqlStart.plusMinutes(duration);
            
            // Updating the object values
            this.setStartTime(sqlStart);
            this.setEndTime(sqlEnd);
            this.setStartTimeFormatted();
            this.setStartTimeCalendarFormatted();
            this.setEndTimeFormatted();
            this.setEndTimeCalendarFormatted();
            // Configuring the prepared statement
            pstmnt.setInt(1, this.getCustId());
            pstmnt.setString(2, this.getTitle());
            pstmnt.setString(3, this.getDescription());
            pstmnt.setString(4, this.getLocation());
            pstmnt.setString(5, this.getContact());
            pstmnt.setString(6, this.getType());
            pstmnt.setObject(7, sqlStart);
            pstmnt.setObject(8, sqlEnd);
            pstmnt.setString(9, currUser);
            pstmnt.setInt(10, appId);

            try {
                pstmnt.executeUpdate();
            } catch (SQLException SqlEx) {
                SqlEx.printStackTrace();
                String err = "There was an error updating the appointment "
                        + "record.";
                SQLException ex = new SQLException(err);
                throw ex;
            }
        } else {
            String err = String.format("Application state does not permit "
                    + "updating this Appointment record."
                    + "\n Application Operation = %s"
                    + "Edit Mode = %s", appOperation,
                    ApplicationState.getEditMode());

            IllegalStateException ex = new IllegalStateException(err);
            throw ex;
        }
    }
    
    // Delete this appointment from SQL
    public void deleteAppointmentRecord() {
        int appId = this.getId();
        String deleteQuery = String.format("DELETE FROM appointment"
                + " WHERE appointmentId = %d", appId);

        SQLConnectionHandler sql = new SQLConnectionHandler();
        Connection conn = sql.getSqlConnection();
        try {
            Statement stmnt = conn.createStatement();
            stmnt.execute(deleteQuery);
        } catch (SQLException SqlEx) {
            SqlEx.printStackTrace();
        } finally {
            sql.closeSqlConnection();
        }

    }
}
