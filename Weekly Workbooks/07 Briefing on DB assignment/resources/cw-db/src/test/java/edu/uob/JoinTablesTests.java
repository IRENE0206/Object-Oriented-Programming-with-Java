package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class JoinTablesTests {
    /*
    <Join>  ::=  "JOIN " [TableName] " AND " [TableName] " ON " [AttributeName] " AND " [AttributeName]
     */

    private DBServer server;
    SyntaxConstructor syntaxConstructor;
    String randomDatabaseName;
    String randomTableName1;
    String randomTableName2;

    @BeforeEach
    public void setup() {
        server = new DBServer();
        syntaxConstructor = new SyntaxConstructor();
        randomDatabaseName = syntaxConstructor.randomNameGenerator();
        sendCommandToServer(syntaxConstructor.createDataBaseCommand(randomDatabaseName));
        sendCommandToServer(syntaxConstructor.useCommand(randomDatabaseName));
        randomTableName1 = syntaxConstructor.randomNameGenerator();
        randomTableName2 = syntaxConstructor.randomNameGenerator();
    }

    private String sendCommandToServer(String command) {
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> { return server.handleCommand(command);},
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testJoinTables() {

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName1, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Clive', 20, FALSE"));

        String response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, "id", "id"));
        assertTrue(response.contains("[ERROR]"), "Non-existing table");

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName2, "tutor, studentID"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Olivia', 4"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Jessica', 2"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Jessica', 3"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Emily', 5"));

        response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, "id", "tuition"));
        assertTrue(response.contains("[ERROR]"), "Non-existing attribute");
        response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, "id", "studentID"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".id"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".id"),
                "The table returned by a JOIN should not contain the original ids");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".studentID"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".name"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".tutor"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".pass"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(response.contains("id"),
                "The table returned by a JOIN should contain newly generated id");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Steve"), "Wrong join results");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Emily"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Clive"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Olivia"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Jessica"), "Wrong join results");

        response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, randomTableName1 + "." + "id", "studentID"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".id"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".id"),
                "The table returned by a JOIN should not contain the original ids");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".studentID"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".name"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".tutor"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".pass"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(response.contains("id"),
                "The table returned by a JOIN should contain newly generated id");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Steve"), "Wrong join results");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Emily"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Clive"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Olivia"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Jessica"), "Wrong join results");


        response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, randomTableName1 + "." + "id", randomTableName2 + "." + "studentID"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".id"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".id"),
                "The table returned by a JOIN should not contain the original ids");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".studentID"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".name"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".tutor"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".pass"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(response.contains("id"),
                "The table returned by a JOIN should contain newly generated id");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Steve"), "Wrong join results");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Emily"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Clive"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Olivia"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Jessica"), "Wrong join results");

        response = sendCommandToServer(syntaxConstructor.joinCommand(randomTableName1, randomTableName2, "id", randomTableName2 + "." + "studentID"));
        assertTrue(response.contains("[OK]"), "A valid query was made, however an [OK] tag was not returned");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".id"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".id"),
                "The table returned by a JOIN should not contain the original ids");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".studentID"),
                "The table returned by a JOIN should not contain the columns joined on");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".name"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + ".tutor"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + ".pass"),
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name");
        assertTrue(response.contains("id"),
                "The table returned by a JOIN should contain newly generated id");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Steve"), "Wrong join results");
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, "Emily"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Dave"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Bob"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Clive"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Olivia"), "Wrong join results");
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, "Jessica"), "Wrong join results");

        sendCommandToServer((syntaxConstructor.dropDatabaseCommand(randomDatabaseName)));
    }
}
