package ru.example;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Date;
import java.util.logging.*;

public class Server {
    private static final Logger logger = Logger.getLogger("ServerLog");

    public static void main(String[] args) {
        setupLogger();
        int port = getPortFromSettings();
        ExecutorService executor = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler client = new ClientHandler(clientSocket);
                executor.execute(client);
            }
        } catch (IOException e) {
            logger.severe("Error starting server: " + e.getMessage());
        }
    }

    private static int getPortFromSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("server.port")) {
                    return Integer.parseInt(line.split("=")[1]);
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading settings: " + e.getMessage());
        }
        return 8080; // Default port
    }

    private static void setupLogger() {
        try {
            FileHandler fh = new FileHandler("server.log", true);
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public String format(LogRecord record) {
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
}