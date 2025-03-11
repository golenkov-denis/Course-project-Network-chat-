package ru.example;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.Date;

public class LoggerService {
    private final Logger logger;

    public LoggerService(String name) {
        logger = Logger.getLogger(name);
        setupLogger();
    }

    private void setupLogger() {
        try {
            FileHandler fh = new FileHandler("server.log", true);
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public String format(java.util.logging.LogRecord record) {
                    return String.format("[%1$tF %1$tT] [%2$s] %3$s%n",
                            new Date(record.getMillis()),
                            record.getLevel(),
                            record.getMessage());
                }
            });
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            logger.severe("Error setting up logger: " + e.getMessage());
        }
    }

    public void info(String message) {
        logger.info(message);
    }

    public void severe(String message) {
        logger.severe(message);
    }
}
