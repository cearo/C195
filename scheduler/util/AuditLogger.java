
package scheduler.util;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Cory
 * This class is used as a logger for the program. It is initialized in the
 * main method in Scheduler.java.
 */
public class AuditLogger {
    private static final String FILE_NAME = "auditLogin.txt";
    private static final Logger LOGGER = Logger.
            getLogger(AuditLogger.class.getName());
    
    public static void initialize() {
        //Handler fileHandler = null;
        //SimpleFormatter simpleFormatter = null;
        
        try {
            // This will create the log file. The true arg tells the fileHandler
            // to append new entries to the existing file.
            Handler fileHandler = new FileHandler(FILE_NAME, true);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            // Configuring the logger with the formatter
            fileHandler.setFormatter(simpleFormatter);
            // Configuring log levels
            fileHandler.setLevel(Level.INFO);
            LOGGER.setLevel(Level.INFO);
            // Configuring logger with  the File Handler
            LOGGER.addHandler(fileHandler);
        }
        catch(IOException IOEx) {
            LOGGER.log(Level.SEVERE, "Error occurred when creating FileHandler",
                    IOEx);
        }
    }
    
    public static Logger getLogger() {
        return LOGGER;
    }
}
