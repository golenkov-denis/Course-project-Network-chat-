package ru.example;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Date;
import java.util.logging.*;

public class Client {
    private static final Logger logger = Logger.getLogger("ClientLog");

    public static void main(String[] args) {
        setupLogger();
        String host = getHostFromSettings();
        int port = getPortFromSettings();

        try (Socket socket = new Socket(host, port);
             Scanner scanner = new Scanner(System.in);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            System.out.print("Enter your username: ");
            String username = scanner.nextLine();
            out.println(username);

            Thread inputThread = new Thread(() -> {
                while (true) {
                    System.out.print("You: ");
                    String message = scanner.nextLine();
                    if (message.equalsIgnoreCase("/exit")) {
                        break;
                    }
                    out.println(message);
                }
            });
            inputThread.start();

            while (true) {
                String response = in.readLine();
                if (response == null) {
                    break;
                }
                System.out.println("Server: " + response);
            }
        } catch (UnknownHostException e) {
            logger.severe("Unknown host: " + e.getMessage());
        } catch (IOException e) {
            logger.severe("Error connecting to server: " + e.getMessage());
        }
    }

    private static String getHostFromSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("server.host")) {
                    return line.split("=")[1];
                }
            }
        } catch (IOException e) {
            logger.severe("Error reading settings: " + e.getMessage());
        }
        return "localhost"; // Default host
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
            FileHandler fh = new FileHandler("client.log", true);
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
