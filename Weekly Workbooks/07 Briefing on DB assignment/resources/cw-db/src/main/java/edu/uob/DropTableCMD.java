package edu.uob;

import java.io.File;

public class DropTableCMD extends DBCmd {
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
            return generateErrorMessage("Table " + this.tableName + " does not exist");
        }
        if (fileToDrop.delete()) {
            return getQueryResults(null);
        }
        return generateErrorMessage("Failed to delete database " + this.tableName);
    }
}
