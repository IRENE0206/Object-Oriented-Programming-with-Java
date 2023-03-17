package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Condition;

public abstract class DBCmd {
    String errorTag = "[ERROR] ";
    String okTag = "[OK]\n";
    String tabTileExtension = ".tab";
    String commandType;
    String databaseName;
    String databasePath;
    String tableName;
    List<String> colNames;
    List<String> attributeList;
    List<String> tableNames;
    List<Condition> conditions;

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
    public String tableToFile() {

    }
}

