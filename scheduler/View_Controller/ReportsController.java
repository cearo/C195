/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.View_Controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import scheduler.util.SQLConnectionHandler;
import scheduler.Model.Appointment;

/**
 * FXML Controller class
 *
 * @author Cory
 */
public class ReportsController implements Initializable {

    @FXML
    private ChoiceBox<String> reportChoice;
    @FXML
    private TextArea reportWindow;
    
    private final String[] REPORT_OPTIONS = {"App Types by Month",
                                            "Consultant Schedules",
                                            "Location App Count"};
    private final String COUNT_REPORT_TEMPLATE = 
            "\n"
            + "\n****************"
            + "\n***  %s  ***"
            + "\n****************"
            +"\n %s : %s  Count : %d";
    private final String ADD_COUNT_LINE = 
            "\n %s : %s   Count : %d";
    private final String SCHEDULE_REPORT_TEMPLATE = 
            "\n"
            + "\n****************"
            + "\n***  %s  ***"
            + "\n****************"
            +"\n %s";
    private final String ADD_SCHEDULE_LINE = "\n %s";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        reportChoice.getItems().setAll(REPORT_OPTIONS);
        reportChoice.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelect, newSelect) -> {
                    reportWindow.clear();
                    String choice = newSelect.trim();
                    reportExecute(choice);
                });
    }

    public void reportExecute(String report) {
        final StringBuilder REPORT_BUILDER = new StringBuilder();
        SQLConnectionHandler sql = new SQLConnectionHandler();
        ResultSet result;
        switch(report) {
            
            case "App Types by Month":
                final String TYPE_BY_MONTH =
                        "SELECT MONTH(start) as 'Month',"
                        + " type, COUNT(appointmentId) as 'Count'"
                        + " FROM appointment GROUP BY 1, 2"
                        + " ORDER BY 1;";
                
                result = sql.executeQuery(TYPE_BY_MONTH);
                
                int prevMonthNum = 0;
                
                try {
                    while (result.next()) {
                        int monthNum = result.getInt("Month");
                        String type = result.getString("type");
                        int count = result.getInt("Count");
                        if(prevMonthNum == monthNum) {
                            String rptLine = String.format(ADD_COUNT_LINE, 
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                        else {
                            prevMonthNum = monthNum;
                            String rptLine = String.format(
                                    COUNT_REPORT_TEMPLATE, 
                                    getMonthFromInt(monthNum),
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                
                break;
            case "Consultant Schedules":
                final String CONSULTANT_SCHEDULES =
                        "SELECT us.userName, app.start"
                        + " FROM user us INNER JOIN appointment app"
                        + " ON(us.userId = app.userId)"
                        + " ORDER BY 1, 2";
                
                result = sql.executeQuery(CONSULTANT_SCHEDULES);
                String prevConsultant = "";
                
                try {
                    while(result.next()) {
                        String consultant = result.getString("userName");
                        Timestamp appStart = result.getTimestamp("start");
                        String startFmt = 
                                Appointment.DT_CALENDAR_FORMATTER.
                                        format(appStart);
                        if(consultant.equals(prevConsultant)) {
                            String rptLine = String.format(ADD_SCHEDULE_LINE,
                                    startFmt);
                            REPORT_BUILDER.append(rptLine);
                        }
                        else {
                            prevConsultant = consultant;
                            String rptLine = String.format(
                                    SCHEDULE_REPORT_TEMPLATE,
                                    consultant, startFmt);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                break;
            case "Location App Count":
                final String LOCATION_APPOINTMENTS = 
                        "SELECT location, type, COUNT(appointmentId) AS 'Count'"
                        + " FROM appointment"
                        + " GROUP BY 1, 2"
                        + " ORDER BY 1;";
                String prevLocation = "";
                
                result = sql.executeQuery(LOCATION_APPOINTMENTS);
                
                try {
                    while(result.next()) {
                        String location = result.getString("location");
                        String type = result.getString("type");
                        int count = result.getInt("Count");
                        
                        if(location.equals(prevLocation)) {
                            String rptLine = String.format(ADD_COUNT_LINE, 
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                        else {
                            prevLocation = location;
                            String rptLine = String.format(
                                    COUNT_REPORT_TEMPLATE,
                                    location, "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                break;
        }
        sql.closeSqlConnection();
    }
    private String getMonthFromInt(int mNum) {
        String month = null;
        DateFormatSymbols dFmtSym = new DateFormatSymbols();
        String[] months = dFmtSym.getMonths();
        if(mNum >= 1 && mNum <= 12) {
            month = months[mNum - 1];
        }
        return month;
    }
}
