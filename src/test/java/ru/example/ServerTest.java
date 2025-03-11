package ru.example;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.*;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {

    @Test
    void testServerResponse() throws IOException {
        try (Socket socket = new Socket("localhost", 8080);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String response = in.readLine();
            assertNotNull(response);
        }
    }
}