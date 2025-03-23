package ru.example;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {
    private static final Logger serverLogger = Logger.getLogger("ServerLogger");
    private static final Logger clientLogger = Logger.getLogger("ClientLogger");

    static {
        setupLogger(serverLogger, "server.log");
        setupLogger(clientLogger, "client.log");
    }

    private static void setupLogger(Logger logger, String fileName) {
        try {
            FileHandler fh = new FileHandler(fileName, true);
            fh.setFormatter(new SimpleFormatter() {
                private final SimpleDateFormat dateFormat =
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                @Override
                public String format(LogRecord record) {
                    return String.format("[%s] [%s] %s%n",
                            dateFormat.format(new Date(record.getMillis())),
                            record.getLevel().getName(),
                            record.getMessage());
                }
            });
            logger.addHandler(fh);
            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Ошибка настройки логгера: " + e.getMessage());
        }
    }

    public static void logServer(String message) {
        serverLogger.info(message);
    }

    public static void logClient(String message) {
        clientLogger.info(message);
    }
}




