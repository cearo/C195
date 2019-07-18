/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import scheduler.util.AuditLogger;

/**
 *
 * @author Cory
 */
public class Scheduler extends Application {
    public static final String BASE_FOLDER_PATH = "/scheduler/View_Controller/";
    
    @Override
    public void start(Stage primaryStage) {
        try{
            Parent root = FXMLLoader.load(getClass().getResource(
                    BASE_FOLDER_PATH + "Login.fxml"
                )
            );
            Scene scene = new Scene(root);
            primaryStage.setTitle("Scheduler");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
        catch (IOException ex){
            System.out.println("Error opening file in: " + BASE_FOLDER_PATH);
        }
    }

    public static void main(String[] args) {
        AuditLogger.initialize();
        launch(args);
    }
    
}
