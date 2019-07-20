package scheduler.View_Controller;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormatSymbols;
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
 * This class controls the users interactions with Reports.fxml
 */
public class ReportsController implements Initializable {

    @FXML
    private ChoiceBox<String> reportChoice;
    @FXML
    private TextArea reportWindow;
    // The static report options available
    private final String[] REPORT_OPTIONS = {"App Types by Month",
                                            "Consultant Schedules",
                                            "Location App Count"};
    // The template string for the report results
    private final String COUNT_REPORT_TEMPLATE = 
            "\n"
            + "\n****************"
            + "\n***  %s  ***"
            + "\n****************"
            +"\n %s : %s  Count : %d";
    // If there's more than one result this will be appended below the 
    // above template
    private final String ADD_COUNT_LINE = 
            "\n %s : %s   Count : %d";
    // The format template for a user schedule
    private final String SCHEDULE_REPORT_TEMPLATE = 
            "\n"
            + "\n****************"
            + "\n***  %s  ***"
            + "\n****************"
            +"\n %s";
    // If there's more than one result this will be appended below the
    // above template
    private final String ADD_SCHEDULE_LINE = "\n %s";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Setting the user's reporting options
        reportChoice.getItems().setAll(REPORT_OPTIONS);
        // This Lambda will add a listener detecting the user has chosen
        // a different report.
        reportChoice.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelect, newSelect) -> {
                    // Clear the previous report result
                    reportWindow.clear();
                    // Getting the chosen report choice and trimming any blanks
                    String choice = newSelect.trim();
                    // Execute the chosen report
                    reportExecute(choice);
                });
    }
    
    // This method will take in the users chosen report and execute it
    public void reportExecute(String report) {
        // This StringBuilder contains the report results
        final StringBuilder REPORT_BUILDER = new StringBuilder();
        SQLConnectionHandler sql = new SQLConnectionHandler();
        ResultSet result;
        
        // Determining the report chosen
        switch(report) {
            
            case "App Types by Month":
                // The query string to obtain the appointment types, grouping
                // them by Month and Type, counting how many there are for each
                // grouping and then ordering by the Month.
                final String TYPE_BY_MONTH =
                        "SELECT MONTH(start) as 'Month',"
                        + " type, COUNT(appointmentId) as 'Count'"
                        + " FROM appointment GROUP BY 1, 2"
                        + " ORDER BY 1;";
                
                result = sql.executeQuery(TYPE_BY_MONTH);
                
                // Each time a new month is encountered, it's month of the year
                // value will be stored to detect if a result is within the 
                // same month as the previous result. If not, it will change
                // to the next new month.
                int prevMonthNum = 0;
                
                try {
                    while (result.next()) {
                        // The month of the year value
                        int monthNum = result.getInt("Month");
                        String type = result.getString("type");
                        int count = result.getInt("Count");
                        // Is this result within the same month as the previous?
                        if(prevMonthNum == monthNum) {
                            // Add a new line the the current section for the month
                            String rptLine = String.format(ADD_COUNT_LINE, 
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                        // This result is in a different Month
                        else {
                            // Setting the new month of the year value to be
                            // the comparator for the next result
                            prevMonthNum = monthNum;
                            // Start a new report section for the new month
                            String rptLine = String.format(
                                    COUNT_REPORT_TEMPLATE, 
                                    getMonthFromInt(monthNum),
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    // Present the report results to the user
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                break;
                
            case "Consultant Schedules":
                // This query string will obtain the appointments for each
                // user, ordering them by the username and then the start time
                final String CONSULTANT_SCHEDULES =
                        "SELECT us.userName, app.start"
                        + " FROM user us INNER JOIN appointment app"
                        + " ON(us.userId = app.userId)"
                        + " ORDER BY 1, 2";
                
                result = sql.executeQuery(CONSULTANT_SCHEDULES);
                // Each time a new consultant (user) is encountered its username
                // value will be stored here to serve as a comparator for the
                // next record to see if it's the same user.
                String prevConsultant = "";
                
                try {
                    while(result.next()) {
                        String consultant = result.getString("userName");
                        Timestamp appStart = result.getTimestamp("start");
                        // Formatting the times to match what the user would 
                        // see on the calendar
                        String startFmt = 
                                Appointment.DT_CALENDAR_FORMATTER.
                                        format(appStart);
                        // Is this the same user as the last record?
                        if(consultant.equals(prevConsultant)) {
                            // Yes it is, add a new line
                            String rptLine = String.format(ADD_SCHEDULE_LINE,
                                    startFmt);
                            REPORT_BUILDER.append(rptLine);
                        }
                        // No, this isn't he same user.
                        else {
                            // Setting the value of the new consultant to use
                            // as a comparator for the next record
                            prevConsultant = consultant;
                            // Start a new section for this user
                            String rptLine = String.format(
                                    SCHEDULE_REPORT_TEMPLATE,
                                    consultant, startFmt);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    // Present the results to the user
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                break;
                
            case "Location App Count":
                // This query string will group appointments by location and
                // type and then present a count of that grouping, ordering
                // the result set by location
                final String LOCATION_APPOINTMENTS = 
                        "SELECT location, type, COUNT(appointmentId) AS 'Count'"
                        + " FROM appointment"
                        + " GROUP BY 1, 2"
                        + " ORDER BY 1;";
                
                // Each time a new location is encountered its value will be
                // stored here to serve as a comparator for the next record to
                // see if the appointment is at the same location as the previous
                String prevLocation = "";
                
                result = sql.executeQuery(LOCATION_APPOINTMENTS);
                
                try {
                    while(result.next()) {
                        String location = result.getString("location");
                        String type = result.getString("type");
                        int count = result.getInt("Count");
                        // Is this the same location as the previous appointment?
                        if(location.equals(prevLocation)) {
                            // Yes it is, so add a new line
                            String rptLine = String.format(ADD_COUNT_LINE, 
                                    "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                        // This is a new appointment location
                        else {
                            // Set the new appointment location as a comparator
                            // for the next appointment record
                            prevLocation = location;
                            // Start a new section for this location
                            String rptLine = String.format(
                                    COUNT_REPORT_TEMPLATE,
                                    location, "Type", type, count);
                            REPORT_BUILDER.append(rptLine);
                        }
                    }
                    // Present the results to the user
                    reportWindow.setText(REPORT_BUILDER.toString());
                }
                catch(SQLException SqlEx) {
                    SqlEx.printStackTrace();
                }
                break;
        }
        sql.closeSqlConnection();
    }
    // This method will obtain the Month based on an Integer which should be
    // a month of the year value.
    private String getMonthFromInt(int mNum) {
        // Null will be returned if a number outside the 1, 12 range is submitted
        String month = null;
        DateFormatSymbols dFmtSym = new DateFormatSymbols();
        // A string array of each month name
        String[] months = dFmtSym.getMonths();
        // Making sure we won't get an IndexOutOfBounds exception
        if(mNum >= 1 && mNum <= 12) {
            // Subtracting one from the month number to appropriately call the
            // intended month index
            month = months[mNum - 1];
        }
        return month;
    }
}
