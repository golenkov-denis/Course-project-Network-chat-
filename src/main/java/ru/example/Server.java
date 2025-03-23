package ru.example;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    private static final List<ClientHandler> clients = new CopyOnWriteArrayList<>();
    private ServerSocket serverSocket;
    private int port;

    public void start() {
        loadSettings();
        try {
            serverSocket = new ServerSocket(port);
            log("Сервер запущен на порту " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            log("Ошибка сервера: " + e.getMessage());
        }
    }

    void broadcastMessage(String message, ClientHandler sender) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String formattedMessage = "[" + sdf.format(new Date()) + "] " + message;

        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(formattedMessage);
            }
        }
        log(formattedMessage);
    }

    void removeClient(ClientHandler client) {
        clients.remove(client);
    }

    private void loadSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader("settings.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("server.port")) {
                    port = Integer.parseInt(line.split("=")[1].trim());
                }
            }
        } catch (IOException | NumberFormatException e) {
            port = 8080;
            log("Используется порт по умолчанию: 8080");
        }
    }

    private void log(String message) {
        try (FileWriter fw = new FileWriter("server.log", true)) {
            fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) +
                    " [INFO] " + message + "\n");
        } catch (IOException e) {
            System.err.println("Ошибка записи лога: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Server server;
    private String username;

    public ClientHandler(Socket socket, Server server) throws IOException {
        this.clientSocket = socket;
        this.server = server;
        this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run() {
        try {
            username = in.readLine();
            server.broadcastMessage(username + " присоединился к чату", this);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if ("/exit".equalsIgnoreCase(inputLine)) break;
                server.broadcastMessage(username + ": " + inputLine, this);
            }
        } catch (IOException e) {
            System.err.println("Ошибка клиента: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                server.removeClient(this);
                server.broadcastMessage(username + " покинул чат", this);
            } catch (IOException e) {
                System.err.println("Ошибка закрытия сокета: " + e.getMessage());
            }
        }
    }

    void sendMessage(String message) {
        out.println(message);
    }
}









