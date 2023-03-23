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
        File databaseToUse = new File(this.databasePath);
        if (databaseToUse.isDirectory()) {
            dbServer.setDatabasePath(this.databasePath);
            return getQueryResults(null);
        }
        return generateErrorMessage("Database " + this.databaseName + " is not an existing directory");
    }
}
