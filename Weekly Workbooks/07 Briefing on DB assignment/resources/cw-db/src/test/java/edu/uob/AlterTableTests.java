package edu.uob;

import org.junit.jupiter.api.BeforeEach;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

public class AlterTableTests {
    /*
    changes the structure (columns) of an existing table
     */
    // TODO
    private DBServer server;

    @BeforeEach
    public void setup() {
        server = new DBServer();
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }


}
