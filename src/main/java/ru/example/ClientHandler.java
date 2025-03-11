package ru.example;

import java.io.*;
import java.net.*;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {
    private static final Logger logger = Logger.getLogger("ServerLog");
    private final Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            String username = in.readLine();
            logger.info("Client connected: " + username);

            while (true) {
                String message = in.readLine();
                if (message == null || message.equalsIgnoreCase("/exit")) {
                    break;
                }
                logger.info("Received message from " + username + ": " + message);
                out.println("Server received your message");
            }
        } catch (IOException e) {
            logger.severe("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.severe("Error closing socket: " + e.getMessage());
            }
        }
    }
}
