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
    private SyntaxConstructor syntaxConstructor;
    private String randomDatabaseName;
    private String randomTableName1;
    private String randomTableName2;

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
        return assertTimeoutPreemptively(Duration.ofMillis(1000), () -> {
            return server.handleCommand(command); },
                "Server took too long to respond (probably stuck in an infinite loop)");
    }

    @Test
    public void testJoinTables() {
        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName1, "name, mark, pass"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Steve', 65, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Dave', 55, TRUE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Bob', 35, FALSE"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName1, "'Clive', 20, FALSE"));

        String id = "id";
        String response = sendCommandToServer(
                syntaxConstructor.joinCommand(
                        randomTableName1, randomTableName2, id, id));
        assertTrue(response.contains("[ERROR]"), "Non-existing table");

        sendCommandToServer(syntaxConstructor.createTableCommand(randomTableName2, "tutor, studentID"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Olivia', 4"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Jessica', 2"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Jessica', 3"));
        sendCommandToServer(syntaxConstructor.insertCommand(randomTableName2, "'Emily', 5"));

        response = sendCommandToServer(
                syntaxConstructor.joinCommand(
                        randomTableName1, randomTableName2, id, "tuition"));
        assertTrue(response.contains("[ERROR]"), "Non-existing attribute");
        String studentID = "studentID";
        response = sendCommandToServer(
                syntaxConstructor.joinCommand(randomTableName1, randomTableName2, id, studentID));
        String okTag = "[OK]";
        String okTagMessage = "A valid query was made, however an [OK] tag was not returned";
        assertTrue(response.contains(okTag), okTagMessage);
        String shouldNotContainJoinedOnColumn =
                "The table returned by a JOIN should not contain the columns joined on";
        String idTail = ".id";
        assertFalse(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + idTail),
                shouldNotContainJoinedOnColumn);
        String shouldNotContainOriginalIDs = "The table returned by a JOIN should not contain the original ids";
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + idTail),
                shouldNotContainOriginalIDs);
        String studentIDTail = ".studentID";
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + studentIDTail),
                shouldNotContainJoinedOnColumn);
        String columnNameFormat =
                "The table returned by a JOIN should contain the columns names joined by table name . attribute name";
        String nameTail = ".name";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + nameTail),
                columnNameFormat);
        String tutorTail = ".tutor";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + tutorTail),
                columnNameFormat);
        String passTail = ".pass";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + passTail),
                columnNameFormat);
        String shouldContainNewIDs = "The table returned by a JOIN should contain newly generated id";
        assertTrue(response.contains(id), shouldContainNewIDs);
        String wrongJoinResults = "Wrong join results";
        String steve = "Steve";
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, steve), wrongJoinResults);
        String emily = "Emily";
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, emily), wrongJoinResults);
        String dave = "Dave";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, dave), wrongJoinResults);
        String bob = "Bob";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, bob), wrongJoinResults);
        String clive = "Clive";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, clive), wrongJoinResults);
        String olivia = "Olivia";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, olivia), wrongJoinResults);
        String jessica = "Jessica";
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, jessica), wrongJoinResults);

        response = sendCommandToServer(
                syntaxConstructor.joinCommand(
                        randomTableName1, randomTableName2, randomTableName1 + "." + id, studentID));
        assertTrue(response.contains(okTag), okTagMessage);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + idTail),
                shouldNotContainJoinedOnColumn);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + idTail),
                shouldNotContainOriginalIDs);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + studentIDTail),
                shouldNotContainJoinedOnColumn);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + nameTail),
                columnNameFormat);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + tutorTail),
                columnNameFormat);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + passTail),
                columnNameFormat);
        assertTrue(response.contains(id), shouldContainNewIDs);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, steve), wrongJoinResults);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, emily), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, dave), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, bob), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, clive), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, olivia), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, jessica), wrongJoinResults);


        response = sendCommandToServer(
                syntaxConstructor.joinCommand(
                        randomTableName1, randomTableName2,
                        randomTableName1 + "." + id, randomTableName2 + "." + studentID));
        assertTrue(response.contains(okTag), okTagMessage);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + idTail),
                shouldNotContainJoinedOnColumn);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + idTail),
                shouldNotContainOriginalIDs);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + studentIDTail),
                shouldNotContainJoinedOnColumn);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + nameTail),
                columnNameFormat);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + tutorTail),
                columnNameFormat);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + passTail),
                columnNameFormat);
        assertTrue(response.contains(id), shouldContainNewIDs);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, steve), wrongJoinResults);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, emily), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, dave), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, bob), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, clive), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, olivia), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, jessica), wrongJoinResults);

        response = sendCommandToServer(
                syntaxConstructor.joinCommand(
                        randomTableName1, randomTableName2, id, randomTableName2 + "." + studentID));
        assertTrue(response.contains(okTag), okTagMessage);
        assertFalse(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + idTail),
                shouldNotContainJoinedOnColumn);
        assertFalse(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + idTail),
                shouldNotContainOriginalIDs);
        assertFalse(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + studentIDTail),
                shouldNotContainJoinedOnColumn);
        assertTrue(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + nameTail),
                columnNameFormat);
        assertTrue(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName2 + tutorTail),
                columnNameFormat);
        assertTrue(
                syntaxConstructor.stringContainsCaseInsensitively(response, randomTableName1 + passTail),
                columnNameFormat);
        assertTrue(response.contains(id), shouldContainNewIDs);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, steve), wrongJoinResults);
        assertFalse(syntaxConstructor.stringContainsCaseInsensitively(response, emily), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, dave), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, bob), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, clive), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, olivia), wrongJoinResults);
        assertTrue(syntaxConstructor.stringContainsCaseInsensitively(response, jessica), wrongJoinResults);

        sendCommandToServer((syntaxConstructor.dropDatabaseCommand(randomDatabaseName)));
    }
}
