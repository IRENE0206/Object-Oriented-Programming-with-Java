package edu.uob;

import java.io.File;

public class DropTableCMD extends DropCMD {
    public DropTableCMD(String tableName) {
        super();
        this.tableName = tableName;
    }
    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        File fileToDrop = new File(this.tableFilePath);
        if (!fileToDrop.isFile()) {
            return errorMessage("Table " + this.tableName + " does not exist");
        }
        if (fileToDrop.delete()) {
            return getQueryResults("");
        }
        return errorMessage("Failed to delete database " + this.tableName);
    }
}
