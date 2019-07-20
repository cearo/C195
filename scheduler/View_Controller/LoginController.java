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
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.Model.LoginFailureException;
import scheduler.Scheduler;
import scheduler.util.ApplicationState;
import scheduler.util.AuditLogger;
import scheduler.util.SQLConnectionHandler;

/**
 * FXML Controller class
 *
 * @author Cory
 * This class controls the interactions between the user and Login.fxml
 */
public class LoginController implements Initializable {

    @FXML
    private TextField userNameField;
    @FXML
    private Button loginButton;
    @FXML
    private Button cancelButton;
    @FXML
    private PasswordField passwordField;
    // Stores the user's locale
    private Locale locale;
    
    // Login error messages. Variables w/ EN appended to their name are English
    // and those with SP appended are Spanish.
    final String LOGIN_ERR_EN = "The username and password entered do "
                + "not match any records.";
    final String LOGIN_ERR_SP = "El nombre de usuario y la contraseña "
            + "ingresados ​​no coinciden con ningún registro.";
    final String ERR_TITLE_EN = "Login Failure!";
    final String ERR_TITLE_SP = "¡Fallo de inicio de sesión!";
    final String ERR_HEADER_EN = "Your login attempt has failed!";
    final String ERR_HEADER_SP = "¡Tu intento de inicio de sesión ha "
            + "fallado!";
    final String NOT_BLANK_ERR_EN = "The username and password fields"
            + " are not allowed to be blank.";
    final String NOT_BLANK_ERR_SP = "Los campos de nombre de usuario y "
            + "contraseña no pueden estar en blanco";
    // Getting Logger for the application
    private static final Logger LOGGER = AuditLogger.getLogger();
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // ****Setting the applications default state****
        ApplicationState.setEditMode(false);
        ApplicationState.setCurrentOperation("View");
        TimeZone dbTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
        ApplicationState.setDatabaseTimezone(dbTimeZone);
        ApplicationState.getUserTimeZone();
        // **********************************************
        // Get the Locale of the user
        locale = ApplicationState.getLocale();
    }
    
    @FXML
    private void loginButtonHandler(ActionEvent event) 
            throws LoginFailureException {
        // This will represent whether the logon attempt was successful
        boolean isLoggedIn;
        String username = userNameField.getText();
        String password = passwordField.getText();
        LocalDateTime now = LocalDateTime.now();
        // Valdating that the user actually entered something into the fields
        if(username != null && !username.equals("") && password != null &&
                !password.equals("")) {
            // Validating login credentials
            isLoggedIn = login(username, password);
            // If login was successful
            if(isLoggedIn) {
                // Log the time and user of the successful attempt
                LOGGER.log(Level.INFO, "{0} {1} login successful.", 
                        new Object[]{now.toString(), username});
                // Checking if there's an appointment within the next 15 minutes
                // of the successful logon
                int userId = ApplicationState.getCurrUserId();
                boolean hasAppointment = appointmentChecker(userId);
                // Alerting the user of the appointment
                if(hasAppointment) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("You have an appointment!");
                    alert.setHeaderText("Appointment within 15 minutes!");
                    alert.setContentText("You have an appointment within"
                            + " the next 15 minutes! Be sure to check"
                            + " your Appointments tab for your schedule.");
                    ButtonType okButton = new ButtonType("OK");
                    alert.getButtonTypes().setAll(okButton);
                    alert.show();
                }
                // Loading the main screen
                try {
                    Parent root = FXMLLoader.load(getClass().getResource(
                    Scheduler.BASE_FOLDER_PATH + "MainScreen.fxml"));

                    Stage stage = (Stage) loginButton.getScene().getWindow();
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();
                }
                catch(IOException IOEx) {
                    IOEx.printStackTrace();
                }
            }
            // Logon attempt failed
            else {
                // Log the time and username of the failed attempt
                LOGGER.log(Level.WARNING, "{0} {1} login failed.",
                        new Object[]{now.toString(), username});
                // Getting the locale of the user to determine the logon failure
                // language
                String loc = locale.getCountry();
                String errMsg;
                Alert alert = new Alert(AlertType.ERROR);
                ButtonType okButton = new ButtonType("OK");
                alert.getButtonTypes().setAll(okButton);
                // User is in the US so use English
                if(loc.equals("US")) {
                    alert.setTitle(ERR_TITLE_EN);
                    alert.setHeaderText(ERR_HEADER_EN);
                    errMsg = LOGIN_ERR_EN;
                }
                // Otherwise use Spanish
                else {
                    alert.setTitle(ERR_TITLE_SP);
                    alert.setHeaderText(ERR_HEADER_SP);
                    errMsg = LOGIN_ERR_SP;
                }
                
                alert.setContentText(errMsg);
                alert.showAndWait();
                alert.close();
                throw new LoginFailureException(errMsg);
            }
        }
        // The user didn't enter form data but tried to submit it
        else {
            String loc = locale.getCountry();
            Alert alert = new Alert(AlertType.ERROR);
            ButtonType okButton = new ButtonType("OK");
            alert.getButtonTypes().setAll(okButton);
            if(loc.equals("US")) {
                alert.setTitle(ERR_TITLE_EN);
                alert.setHeaderText(ERR_HEADER_EN);
                alert.setContentText(NOT_BLANK_ERR_EN);
            }
            else {
                alert.setTitle(ERR_TITLE_SP);
                alert.setHeaderText(ERR_HEADER_SP);
                alert.setContentText(NOT_BLANK_ERR_SP);
            }
            
            alert.showAndWait();
            alert.close();
        }
    }
    
    @FXML
    private void cancelButtonHandler(ActionEvent event) {
        // You either login or leave the app
        Platform.exit();
    }
    // This method will submit the users entered credentials to the database
    // and return a validation decision whether the info matches a DB record.
    public boolean login(String username, String password) {
        // Default is the attempt was not successful
        boolean isLoggedIn = false;
        String sqlQuery = "SELECT userId, username, password FROM user WHERE "
                + "username = ? AND password = ?";
        SQLConnectionHandler sql = new SQLConnectionHandler();
        Connection conn = sql.getSqlConnection();
        
        try {
            PreparedStatement pstmnt = conn.prepareCall(sqlQuery);
            pstmnt.setString(1, username);
            pstmnt.setString(2, password);
            ResultSet result = pstmnt.executeQuery();
            if(result.next()) {
                int userId = result.getInt("userId");
                String resultUser = result.getString("username");
                String resultPass = result.getString("password");
                // Valdating whether they match or not
                if(username.equals(resultUser) && password.equals(resultPass)) {
                    ApplicationState.setCurrUserId(userId);
                    ApplicationState.setCurrentUser(username);
                    // Changing the decision to successful as they match
                    isLoggedIn = true;
                }
            }
        }
        catch(SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        return isLoggedIn;
    }
    
    // This method checks whether the user has an appointment in the next 
    // 15 minutes and returns a true if they do.
    public boolean appointmentChecker(int userId) {
        boolean hasAppointment = false;
        String sqlQuery = "SELECT MIN(start) AS 'Start'"
                + " FROM appointment WHERE userId = ? AND "
                + "start >= NOW();";
        SQLConnectionHandler sql = new SQLConnectionHandler();
        Connection conn = sql.getSqlConnection();
        // Getting the time zone info to ensure comparing data matches
        TimeZone userTimeZone = ApplicationState.getUserTimeZone();
        ZoneId userTZId = ZoneId.of(userTimeZone.getID());
        LocalTime currTime = LocalTime.now(userTZId);
        LocalTime appTime = null;
        
        try {
            PreparedStatement pstmnt = conn.prepareCall(sqlQuery);
            pstmnt.setInt(1, userId);
            ResultSet result = pstmnt.executeQuery();
            if(result.next()) {
                Timestamp timestamp = result.getTimestamp("Start");
                if(timestamp != null) {
                     appTime = timestamp.toLocalDateTime().atZone(userTZId).
                        toLocalTime();
                }
            }
        }
        catch (SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        // If a result was obtained
        if(appTime != null) {
            // What's the difference between the two times?
            Duration duration = Duration.between(appTime, currTime);
            // Converting the seconds to minutes
            long diff = duration.getSeconds() / 60;
            // Is the appointment within 15 minutes of the logon attempt?
            // A sub-zero result would indicate the appointment was prior to 
            // the logon attempt, which means it's not pending appointment
            // therefore the user won't be notified of it.
            if(diff <= 15 && diff >= 0) {
                hasAppointment = true;
            }
        }
        
        return hasAppointment;
    }
}
