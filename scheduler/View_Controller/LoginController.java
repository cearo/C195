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
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import scheduler.Scheduler;
import scheduler.util.ApplicationState;
import scheduler.util.SQLConnectionHandler;

/**
 * FXML Controller class
 *
 * @author Cory
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
    
    private Locale locale;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ApplicationState.setEditMode(false);
        ApplicationState.setCurrentOperation("View");
        TimeZone dbTimeZone = TimeZone.getTimeZone("America/Los_Angeles");
        ApplicationState.setDatabaseTimezone(dbTimeZone);
        ApplicationState.getUserTimeZone();
        locale = ApplicationState.getLocale();
    }
    
    @FXML
    private void loginButtonHandler(ActionEvent event) {
        boolean isLoggedIn;
        String username = userNameField.getText();
        String password = passwordField.getText();
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
        
        if(username != null && !username.equals("") && password != null &&
                !password.equals("")) {
            isLoggedIn = login(username, password);
            if(isLoggedIn) {
                int userId = ApplicationState.getCurrUserId();
                boolean hasAppointment = appointmentChecker(userId);
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
            else {
                String loc = locale.getCountry();
                Alert alert = new Alert(AlertType.ERROR);
                ButtonType okButton = new ButtonType("OK");
                alert.getButtonTypes().setAll(okButton);
                if(loc.equals("US")) {
                    alert.setTitle(ERR_TITLE_EN);
                    alert.setHeaderText(ERR_HEADER_EN);
                    alert.setContentText(LOGIN_ERR_EN);
                }
                else {
                    alert.setTitle(ERR_TITLE_SP);
                    alert.setHeaderText(ERR_HEADER_SP);
                    alert.setContentText(LOGIN_ERR_SP);
                }

                alert.showAndWait();
                alert.close();
            }
        }
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
    public boolean login(String username, String password) {
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
                if(username.equals(resultUser) && password.equals(resultPass)) {
                    ApplicationState.setCurrUserId(userId);
                    ApplicationState.setCurrentUser(username);
                    isLoggedIn = true;
                }
            }
        }
        catch(SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        return isLoggedIn;
    }
    
    public boolean appointmentChecker(int userId) {
        boolean hasAppointment = false;
        String sqlQuery = "SELECT MIN(start) AS 'Start'"
                + " FROM appointment WHERE userId = ? AND "
                + "start >= NOW();";
        SQLConnectionHandler sql = new SQLConnectionHandler();
        Connection conn = sql.getSqlConnection();
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
                System.out.println(timestamp);
                appTime = timestamp.toLocalDateTime().atZone(userTZId).
                        toLocalTime();
            }
        }
        catch (SQLException SqlEx) {
            SqlEx.printStackTrace();
        }
        
        if(appTime != null) {
            Duration duration = Duration.between(appTime, currTime);
            long diff = duration.getSeconds() / 60;
            System.out.println(appTime);
            System.out.println(currTime);
            System.out.println(diff);
            if(diff <= 15 && diff >= 0) {
                hasAppointment = true;
            }
        }
        
        return hasAppointment;
    }
}
