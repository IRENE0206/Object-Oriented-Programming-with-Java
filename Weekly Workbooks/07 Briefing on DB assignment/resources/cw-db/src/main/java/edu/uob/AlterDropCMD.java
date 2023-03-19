package edu.uob;

import java.io.File;

public class AlterDropCMD extends AlterCMD {
    public AlterDropCMD(String tableName, String alterType, String attributeName) {
        super(tableName, alterType, attributeName);
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        File fileToAlter = new File(this.tableFilePath);
        if (!fileToAlter.isFile()) {
            return errorMessage(tableName + " doesn't exist");
        }
        String error = tableFileToDBTable();
        if (!error.isEmpty()) {
            return error;
        }
        if (!this.dbTable.containsAttribute(this.attributeName)) {
            return errorMessage(this.attributeName + " does not in table " + this.tableName);
        } else if (this.attributeName.toLowerCase().compareTo("id") == 0) {
            return errorMessage("Cannot drop id");
        }
        this.dbTable.dropCol(this.attributeName);
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return errorMessage("Failed to change table " + this.tableName);
        }
        return getQueryResults("");
    }
}
