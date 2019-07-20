package scheduler.util;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.Model.Appointment;
import scheduler.Model.Customer;
import scheduler.View_Controller.CustomersController;

/**
 *
 * @author Cory
 * This class contains the data source for appointments.
 */
public class DataHandler {
    // This ObservableList is the underlying data source for the
    // appointments TablView in Appointments.fxml
    private static final ObservableList APPOINTMENTS =
            FXCollections.observableArrayList();
    // This is the array which contains the list of customers in the database
    private static final ObservableList CUSTOMERS = FXCollections.observableArrayList();
    
    // This getter will populate the list with any appointments that are after
    // the moment it's invoked, only if APPOINTMENTS is empty. Otherwise it will
    // return the current APPOINTMENTS list as the application will handle
    // adding and removing appointments after the initial invocation.
    public static ObservableList getAppointments() {
        
        if(APPOINTMENTS.isEmpty()) {
            // The query string to get all necessary appointment info
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
            
            try {
                PreparedStatement allAppInfoStmnt
                        = conn.prepareStatement(allAppointmentInfo);
                ResultSet allAppInfoRslt = allAppInfoStmnt.executeQuery();
                DataHandler.APPOINTMENTS.clear();

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
                    LocalDateTime appStartDateTime = startSqlDate.toLocalDateTime();
                    LocalDate appStartDate = appStartDateTime.toLocalDate();
                    Timestamp endSqlDate = allAppInfoRslt.getTimestamp("end");
                    LocalDateTime appEndDateTime = endSqlDate.toLocalDateTime();
                    LocalDate appEndDate = appEndDateTime.toLocalDate();
                    String startCalendarFmt = Appointment.DT_CALENDAR_FORMATTER.format(
                            startSqlDate);
                    
                    String startLocaleFmt = Appointment.DT_LOCALE_FORMATTER.format(Date.valueOf(appStartDate));
                    String endCalendarFmt = Appointment.DT_CALENDAR_FORMATTER.format(endSqlDate);
                    String endLocaleFmt = Appointment.DT_LOCALE_FORMATTER.format(Date.valueOf(appEndDate));

                    Appointment newApp = new Appointment(appId, custId, userId,
                            appTitle, appLoc, appType,
                            appContact, appDescr, appStartDateTime,
                            appEndDateTime, startLocaleFmt,
                            endLocaleFmt, startCalendarFmt,
                            endCalendarFmt);
                    DataHandler.APPOINTMENTS.add(newApp);
                }
            } 
            catch (SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
        }
        
        return APPOINTMENTS;
    }
    // This getter method checks if the CUSTOMERS list is is empty and if so
    // it will query the database for all customer records and populate the list
    // with them.
    public static ObservableList getCustomers() {
        if (CUSTOMERS.isEmpty()) {
            String allCustomers = "SELECT * FROM customer ORDER BY customerId;";
            SQLConnectionHandler sql = new SQLConnectionHandler();
            ResultSet result = sql.executeQuery(allCustomers);
            // While there are results to get
            try {
                while (result.next()) {
                    // Getting the customer info from the query
                    int id = result.getInt("customerId");
                    String name = result.getString("customerName");
                    int active = result.getInt("active");
                    int custAddId = result.getInt("addressId");
                    // Creating a new Customer object
                    Customer cust = new Customer(id, name, active, custAddId);
                    // Populating that Customer's Address info. I don't
                    // assign it as I don't currently have use for it, but
                    // I will.
                    cust.getCustomerAddress();
                    // Adding each customer to the array
                    CUSTOMERS.add(cust);
                }
            } catch (SQLException SqlEx) {
                SqlEx.printStackTrace();
            }
        }
        return CUSTOMERS;
    }
    
    
}
