package ru.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ServerTest {
    @Test
    void testServerStart() {
        assertDoesNotThrow(() -> {
            new Thread(() -> new Server().start()).start();
            Thread.sleep(1000);
        });
    }
}

