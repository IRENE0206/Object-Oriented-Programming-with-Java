package edu.uob;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class ReadAndWriteFileTests {
    private DBServer dbServer;
    @BeforeEach
    public void setDbServer() {
        this.dbServer = new DBServer();
    }
    @Test
    public void testReadFromTabFile() {
        File currDir = new File(System.getProperty("user.dir"));
        File resourcesDir = new File(currDir.getParent());
        String peopleTabName = resourcesDir + File.separator + "people.tab";
        File peopleTab = new File(peopleTabName);
        DBTable table = this.dbServer.fileToTable(peopleTabName);
        compareTableContentsAndFile(table, peopleTab);
        table.changeValue(1, "age", "26");
        this.dbServer.tableToFile(table, peopleTab);
        compareTableContentsAndFile(table, peopleTab);

        String shedsTabName = resourcesDir + File.separator + "sheds.tab";
        File shedsTab = new File(shedsTabName);
        table = this.dbServer.fileToTable(shedsTabName);
        compareTableContentsAndFile(table, shedsTab);
        table.changeValue(3, "Height", "1200");
        this.dbServer.tableToFile(table, shedsTab);
        compareTableContentsAndFile(table, shedsTab);
    }

    private void compareTableContentsAndFile(DBTable table, File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader buffReader = new BufferedReader(fileReader);
            try {
                String firstLine = buffReader.readLine();
                if (firstLine.length() == 0) {
                    assertNull(table, "Should be an empty table");
                } else {
                    assertEquals(0, firstLine.compareTo(table.getColNames()));
                    String content
                            = buffReader.lines()
                            .filter(l -> l.length() > 0)
                            .reduce("", (a, b) -> String.join("\n", a, b))
                            .stripLeading();
                    // System.out.println("Content: \n" + content);
                    // System.out.println("Table: \n" + table.getValues());
                    assertEquals(0, content.compareTo(table.getValues()));
                }
            } catch (IOException ioException) {
                System.out.println("Cannot read " + file);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println(file + "Not found");
        }
    }
}
