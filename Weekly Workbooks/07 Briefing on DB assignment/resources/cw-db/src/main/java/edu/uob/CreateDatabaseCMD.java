package edu.uob;

import java.io.File;

public class CreateDatabaseCMD extends CreateCMD {
    public CreateDatabaseCMD(String databaseName) {
        super();
        this.databaseName = databaseName;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromStorageFolderPath(dbServer.getStorageFolderPath());
        File databaseToCreate = new File(this.databasePath);
        if (databaseToCreate.isDirectory()) {
            return errorMessage(this.databaseName + " already exists");
        } else if (databaseToCreate.mkdir()) {
            return getQueryResults("");
        }
        return errorMessage("Failed to create database " + this.databaseName);
    }
}
