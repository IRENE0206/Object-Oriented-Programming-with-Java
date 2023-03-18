package edu.uob;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;

public abstract class DBCmd {
    String errorTag = "[ERROR] ";
    String okTag = "[OK]\n";
    String tabFileExtension = ".tab";
    String commandType;
    String databaseName;
    String databasePath;
    String tableName;
    String tableFilePath;
    List<String> colNames;
    List<String> attributeList;
    List<String> tableNames;
    List<Condition> conditions;
    List<String> values;
    DBTable dbTable;

    public abstract String query(DBServer dbServer);

    void setDatabasePathFromStorageFolderPath(String storageFolderPath) {
        this.databasePath = storageFolderPath + File.separator + this.databaseName;
    }

    void setDatabasePathFromCurrentDatabasePath(String currentDatabasePath) {
        this.databasePath = currentDatabasePath;
    }

    String errorMessage(String message) {
        return errorTag + message;
    }

    String getQueryResults(String results) {
        return okTag + results;
    }

    void setTableFilePath() {
        this.tableFilePath = this.databasePath + File.separator + this.tableName + tabFileExtension;
    }

    String tableFileToDBTable() {
        File fileToRead = new File(this.tableFilePath);
        FileReader reader;
        try {
            reader = new FileReader(fileToRead);
        } catch (FileNotFoundException e) {
            return errorMessage(this.tableName + " not found");
        }
        BufferedReader buffReader = new BufferedReader(reader);
        String firstLine;
        try {
            firstLine = buffReader.readLine();
            if (firstLine.length() > 0) {
                String[] firstLineSplit = firstLine.split("\t");
                this.dbTable = new DBTable(this.tableName, Arrays.stream(firstLineSplit).toList());
                buffReader
                        .lines()
                        .filter(s -> s.length() > 0)
                        .forEach(s -> this.dbTable.addRow(Arrays.stream(s.split("\t")).toList()));
            } else {
                this.dbTable = new DBTable(this.tableName);
            }
            buffReader.close();
            return "";
        } catch (IOException e) {
            return errorMessage("Failed to read table " + this.tableName);
        }
    }
}

