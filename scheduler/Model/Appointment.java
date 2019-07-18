/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.util.Date;
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
 */
public class Appointment {

    private static final TimeZone SQL_TZ
            = ApplicationState.getDatabaseTimeZone();
    private static final String SQL_TZ_ID = SQL_TZ.getID();
    private static final String DT_CALENDAR_FORMAT
            = "EEE, MMM dd, yyyy " + "hh:mm:ss";
    public static final String DT_EU_FORMAT = "dd/MM/yyyy HH:mm:ss";
    public static final String DT_USA_FORMAT = "MM/dd/yyyy HH:mm:ss";
    private static final String SQL_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final LocalTime BUSINESS_START = LocalTime.parse("08:00");
    private static final LocalTime BUSINESS_END = LocalTime.parse("17:00");
    private static final DateTimeFormatter SQL_DATE_FORMATTER
            = DateTimeFormatter.ofPattern(SQL_DATE_FORMAT).withZone(
                    ZoneId.of(SQL_TZ_ID));
    public static final SimpleDateFormat DT_LOCALE_FORMATTER = new SimpleDateFormat();
    public static final SimpleDateFormat DT_CALENDAR_FORMATTER
            = new SimpleDateFormat(DT_CALENDAR_FORMAT);

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
    private Customer customer;
    private Address customerAddress;

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
        //Timestamp timestamp = Timestamp.valueOf(starTime);
        Timestamp timestamp = Timestamp.valueOf(appStartTime);
        String startFmt = DT_LOCALE_FORMATTER.format(timestamp);
        this.startTimeFmt.set(startFmt);
    }

    public StringProperty startTimeFmtProperty() {
        return this.startTimeFmt;
    }

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
        Timestamp timestamp = Timestamp.valueOf(starTime);
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
        Timestamp timestamp = Timestamp.valueOf(end);
        String endFmt = DT_LOCALE_FORMATTER.format(timestamp);
        this.endTimeFmt.set(endFmt);
    }

    public StringProperty endTimeFmtProperty() {
        return this.endTimeFmt;
    }

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
        Timestamp timestamp = Timestamp.valueOf(endTime);
        String endFmt = DT_CALENDAR_FORMATTER.format(timestamp);
        this.endCalFmt.set(endFmt);
    }

    public StringProperty endCalFmtProperty() {
        return this.endCalFmt;
    }

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

    public Address getCustomerAddress() {
        Address addr = null;
        if (this.customerAddress == null && this.customer != null) {
            addr = this.customer.getCustomerAddress();
        } else {
            addr = this.customerAddress;
        }
        return addr;
    }

    public static Appointment addAppointmentRecord(
            ObservableList<Node> children) throws IllegalStateException,
            SQLException, BusinessHoursException, 
            AppointmentOverlapException {
        Appointment app = null;
        String appOperation = ApplicationState.getCurrentOperation();
        String currUser = ApplicationState.getCurrentUser();

        if (appOperation.equals("Add")) {
            SQLConnectionHandler sql = new SQLConnectionHandler();
            Connection conn = sql.getSqlConnection();
            String insertApp = "INSERT INTO appointment"
                    + "(customerId, userId, title, description, location,"
                    + "contact, type, start, end, createDate, createdBy)"
                    + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, NOW(), ?);";
            PreparedStatement pstmnt = conn.prepareStatement(insertApp,
                    Statement.RETURN_GENERATED_KEYS);

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

            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();

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
            
            boolean isBeforeStart = startComboTime.isBefore(BUSINESS_START);
            boolean isAfterEnd = startComboTime.
                    plusMinutes(duration).isAfter(BUSINESS_END);
            
            if (isBeforeStart || isAfterEnd) {
               throw new BusinessHoursException("Appointment is outside"
                       + "business hours."); 
            }
            
            ObservableList appList = DataHandler.getAppointments();
            
            for(int i = 0; i < appList.size(); i++) {
                Appointment apmnt = (Appointment) appList.get(i);
                LocalDate appDate = apmnt.getStartTime().toLocalDate();
                LocalTime appStartTime = apmnt.getStartTime().toLocalTime();
                LocalTime appEndTime = apmnt.getEndTime().toLocalTime();
                
                if( dpDate.equals(appDate) && 
                        (startComboTime.equals(appStartTime) || 
                        startComboTime.plusMinutes(duration).equals(appEndTime))
                   ) {
                    String errMsg = "This appointment overlaps with another."
                            + " Please reschedule to resolve the conflict.";
                    throw new AppointmentOverlapException(errMsg);
                }
            }
            
            pstmnt.setInt(1, custId);
            pstmnt.setInt(2, userId);
            pstmnt.setString(3, appTitle);
            pstmnt.setString(4, appDesc);
            pstmnt.setString(5, appLoc);
            pstmnt.setString(6, appContact);
            pstmnt.setString(7, appType);

            String LocalDateTimeFmt = dpDate.atTime(startComboTime).
                    format(SQL_DATE_FORMATTER);
            LocalDateTime sqlStart = LocalDateTime.parse(LocalDateTimeFmt, SQL_DATE_FORMATTER);

            LocalDateTime sqlEnd = sqlStart.plusMinutes(duration);

            pstmnt.setObject(8, sqlStart);
            pstmnt.setObject(9, sqlEnd);
            pstmnt.setString(10, ApplicationState.getCurrentUser());

            try {
                int rowsAffected = pstmnt.executeUpdate();

                if (rowsAffected == 1) {
                    ResultSet key = pstmnt.getGeneratedKeys();
                    if (key.next()) {
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

    public void updateAppointmentRecord(ObservableList<Node> children)
            throws IllegalStateException, SQLException , 
            BusinessHoursException, AppointmentOverlapException {

        String appOperation = ApplicationState.getCurrentOperation();
        String currUser = ApplicationState.getCurrentUser();

        if (appOperation.equals("Update")) {
            String updateApp = "UPDATE appointment SET customerId = ?, "
                    + "title = ?, description = ?, location = ?, contact = ?,"
                    + "type = ?, start = ?, end = ?, lastUpdate = NOW(),"
                    + "lastUpdateBy = ? WHERE appointmentId = ?";
            SQLConnectionHandler sql = new SQLConnectionHandler();
            Connection conn = sql.getSqlConnection();
            PreparedStatement pstmnt = conn.prepareCall(updateApp);

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
            for (int i = 0; i < children.size(); i++) {
                Node child = children.get(i);
                String childId = child.getId();

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
            
            boolean isBeforeStart = startComboTime.isBefore(BUSINESS_START);
            boolean isAfterEnd = startComboTime.
                    plusMinutes(duration).isAfter(BUSINESS_END);
            
            if (isBeforeStart || isAfterEnd) {
               throw new BusinessHoursException("Appointment is outside"
                       + "business hours."); 
            }
            
            ObservableList appList = DataHandler.getAppointments();
            
            for(int i = 0; i < appList.size(); i++) {
                Appointment apmnt = (Appointment) appList.get(i);
                LocalDate appDate = apmnt.getStartTime().toLocalDate();
                LocalTime appStartTime = apmnt.getStartTime().toLocalTime();
                LocalTime appEndTime = apmnt.getEndTime().toLocalTime();
                
                if( dpDate.equals(appDate) && 
                        (startComboTime.equals(appStartTime) || 
                        startComboTime.plusMinutes(duration).equals(appEndTime))
                   ) {
                    String errMsg = "This appointment overlaps with another."
                            + " Please reschedule to resolve the conflict.";
                    throw new AppointmentOverlapException(errMsg);
                }
            }

            String LocalDateTimeFmt = dpDate.atTime(startComboTime).
                    format(SQL_DATE_FORMATTER);
            LocalDateTime sqlStart
                    = LocalDateTime.parse(LocalDateTimeFmt, SQL_DATE_FORMATTER);

            LocalDateTime sqlEnd = sqlStart.plusMinutes(duration);

            this.setStartTime(sqlStart);
            this.setEndTime(sqlEnd);
            this.setStartTimeFormatted();
            this.setStartTimeCalendarFormatted();
            this.setEndTimeFormatted();
            this.setEndTimeCalendarFormatted();
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
