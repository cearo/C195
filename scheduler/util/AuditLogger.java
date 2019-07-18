/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.io.IOException;
import java.util.Formatter;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Cory
 */
public class AuditLogger {
    private static final String FILE_NAME = "auditLogin.txt";
    private static final Logger LOGGER = Logger.
            getLogger(AuditLogger.class.getName());
    
    public static void initialize() {
        Handler fileHandler = null;
        SimpleFormatter simpleFormatter = null;
        
        try {
            fileHandler = new FileHandler(FILE_NAME, true);
            simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
            fileHandler.setLevel(Level.INFO);
            LOGGER.setLevel(Level.INFO);
            LOGGER.addHandler(fileHandler);
        }
        catch(IOException IOEx) {
            LOGGER.log(Level.SEVERE, "Error occurred when creating FileHandler",
                    IOEx);
        }
    }
    
}
