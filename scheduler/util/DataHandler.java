/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

/**
 *
 * @author Cory
 */
public class DataHandler {

    private static final ObservableList APPOINTMENTS =
            FXCollections.observableArrayList();
    
    public static ObservableList getAppointments() {
        
        if(APPOINTMENTS.isEmpty()) {
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
    
}
