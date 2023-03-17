package edu.uob;

import java.io.File;

public class UseCMD extends DBCmd {
    public UseCMD(String databaseName) {
        this.commandType = "USE";
        this.databaseName = databaseName;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromStorageFolderPath(dbServer.getStorageFolderPath());
        File databaseToUse = new File(databasePath);
        if (databaseToUse.isDirectory()) {
            dbServer.setDatabasePath(databasePath);
            return getQueryResults("");
        }
        return errorMessage("Database " + this.databaseName + " is not an existing directory");
    }
}
