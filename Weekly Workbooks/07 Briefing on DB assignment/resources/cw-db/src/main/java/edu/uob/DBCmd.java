package edu.uob;

import java.util.List;

public abstract class DBCmd {
    String errorTag = "[ERROR] ";
    String okTag = "[OK]\n";
    String commandType;
    String databaseName;
    String databasePath;
    String tableName;
    List<String> attributeList;
    List<String> tableNames;

    public abstract String query(DBServer dbServer);
}

