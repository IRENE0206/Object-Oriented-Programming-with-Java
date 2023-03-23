package edu.uob;

import java.io.File;

public class DropDatabaseCMD extends DropCMD {
    public DropDatabaseCMD(String databaseName) {
        super();
        this.databaseName = databaseName;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromStorageFolderPath(dbServer.getStorageFolderPath());
        File fileToDrop = new File(this.databasePath);
        if (!fileToDrop.isDirectory()) {
            return generateErrorMessage(this.databaseName + " is not an existing directory");
        } else if (failToDeleteFileRecursively(fileToDrop)) {
            return generateErrorMessage("Failed to drop " + this.databaseName);
        }
        return getQueryResults(null);
    }

    private boolean failToDeleteFileRecursively(File fileToDelete) {
        if (fileToDelete.isDirectory()) {
            File[] documents = fileToDelete.listFiles();
            if (documents != null) {
                for (File d : documents) {
                    if (failToDeleteFileRecursively(d)) {
                        return true;
                    }
                }
            }
        }
        return !fileToDelete.delete();
    }
}
