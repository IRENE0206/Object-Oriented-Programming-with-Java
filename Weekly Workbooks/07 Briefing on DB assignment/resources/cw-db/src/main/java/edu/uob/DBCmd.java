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
    List<String> attributeList;
    List<String> tableNames;
    List<Condition> condition;
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
        this.tableFilePath = getTableFilePath(this.tableName);
    }

    String getTableFilePath(String tableName) {
        return this.databasePath + File.separator + tableName + tabFileExtension;
    }

    String tableFileToDBTable(String tableFilePath, DBTable dbTable) {
        File fileToRead = new File(tableFilePath);
        FileReader reader;
        try {
            reader = new FileReader(fileToRead);
        } catch (FileNotFoundException e) {
            return errorMessage("Table " + dbTable.getTableName() + " not found");
        }
        BufferedReader buffReader = new BufferedReader(reader);
        try {
            String firstLine = buffReader.readLine();
            if (firstLine.length() > 0) {
                String[] firstLineSplit = firstLine.split("\t");
                dbTable.setColNamesNoNeedToAddId(Arrays.stream(firstLineSplit).toList());
                // System.out.println("New: ");
                // dbTable.getColNames().forEach(System.out::println);
                buffReader
                        .lines()
                        .filter(s -> s.length() > 0)
                        .forEachOrdered(s -> dbTable.setRows(Arrays.stream(s.split("\t")).toList()));
            }
            buffReader.close();
            return "";
        } catch (IOException e) {
            return errorMessage("Failed to read table " + tableFilePath);
        }
    }

    boolean stringListContainsStringCaseInsensitively(List<String> stringList, String s) {
        for (String str : stringList) {
            if (stringsEqualCaseInsensitively(str, s)) {
                return true;
            }
        }
        return false;
    }

    boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }
}

