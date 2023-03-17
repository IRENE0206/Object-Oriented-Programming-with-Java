package edu.uob;

import java.io.File;
import java.util.List;

public abstract class DBCmd {
    String errorTag = "[ERROR] ";
    String okTag = "[OK]\n";
    String commandType;
    String databaseName;
    String databasePath;
    List<String> tableNames;
    public abstract String query(DBServer dbServer);
    String getDatabasePath(DBServer dbServer) {
        return dbServer.getStorageFolderPath() + File.separator + databaseName;
    }
}

