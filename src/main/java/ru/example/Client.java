package ru.example;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private String username;
    private String host;
    private int port;

    public void start() {
        loadSettings();
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            setupUsername();
            startMessageReceiver();
            startMessageSender();

        } catch (IOException e) {
            log("Ошибка подключения: " + e.getMessage());
        }
    }

    private void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("server.host")) host = line.split("=")[1].trim();
                if (line.startsWith("server.port")) port = Integer.parseInt(line.split("=")[1].trim());
            }
        } catch (IOException | NumberFormatException e) {
            host = "localhost";
            port = 8080;
            log("Используются настройки по умолчанию");
        }
    }

    private void setupUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите имя: ");
        username = scanner.nextLine();
        out.println(username);
    }

    private void startMessageReceiver() {
        new Thread(() -> {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println(message);
                }
            } catch (IOException e) {
                log("Соединение прервано");
            }
        }).start();
    }

    private void startMessageSender() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                if ("/exit".equalsIgnoreCase(message)) {
                    out.println("/exit");
                    break;
                }
                out.println(message);
            }
            System.exit(0);
        }).start();
    }

    private void log(String message) {
        try (FileWriter fw = new FileWriter("client.log", true)) {
            fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                    " [INFO] " + message + "\n");
        } catch (IOException e) {
            System.err.println("Ошибка записи лога: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Client().start();
    }
}






