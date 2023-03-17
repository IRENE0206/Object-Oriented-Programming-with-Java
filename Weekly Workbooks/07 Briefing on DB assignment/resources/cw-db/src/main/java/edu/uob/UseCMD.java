package edu.uob;

import java.io.File;

public class UseCMD extends DBCmd {
    public UseCMD(String databaseName) {
        this.commandType = "USE";
        this.databaseName = databaseName;
    }
    @Override
    public String query(DBServer dbServer) {
        this.databasePath = getDatabasePath(dbServer);
        File databaseToOpen = new File(this.databasePath);
        if (!databaseToOpen.isDirectory()) {
            return errorTag + "Database " + databaseName + " does not exist";
        }
        dbServer.setDatabasePath(databasePath);
        return okTag + "Changed database to " + databaseName;
    }
}
