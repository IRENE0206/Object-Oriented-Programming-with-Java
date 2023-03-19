package edu.uob;

import java.io.File;

public class AlterAddCMD extends AlterCMD {
    public AlterAddCMD(String tableName, String alterType, String attributeName) {
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
        if (this.dbTable.containsAttribute(this.attributeName)) {
            return errorMessage(this.attributeName + " already exists in table " + this.tableName);
        }
        this.dbTable.addCol(this.attributeName);
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return errorMessage("Failed to change table " + this.tableName);
        }
        return getQueryResults("");
    }

}
