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
        String error = tableFileToDBTable(this.tableFilePath, this.dbTable);
        if (error != null) {
            return error;
        } else if (this.dbTable.containsAttribute(this.attributeName)) {
            return errorMessage(this.attributeName + " already exists in table " + this.tableName);
        }
        String error2 = this.dbTable.addCol(this.attributeName);
        if (error2 != null) {
            return errorMessage(error2);
        }
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return errorMessage("Failed to change table " + this.tableName);
        }
        return getQueryResults(null);
    }

}
