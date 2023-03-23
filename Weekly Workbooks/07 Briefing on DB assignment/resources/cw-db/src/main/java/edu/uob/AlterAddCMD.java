package edu.uob;

import java.io.File;

public class AlterAddCMD extends DBCmd {
    private final String attributeName;
    public AlterAddCMD(String tableName, String attrName) {
        this.tableName = tableName;
        this.dbTable = new DBTable(this.tableName);
        this.attributeName = attrName;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        File fileToAlter = new File(this.tableFilePath);
        if (!fileToAlter.isFile()) {
            return generateErrorMessage(tableName + " doesn't exist");
        }
        String error = loadTabFileToDBTable(this.tableFilePath, this.dbTable);
        if (error != null) {
            return error;
        } else if (this.dbTable.containsAttribute(this.attributeName)) {
            return generateErrorMessage(this.attributeName + " already exists in table " + this.tableName);
        }
        String error2 = this.dbTable.addCol(this.attributeName);
        if (error2 != null) {
            return generateErrorMessage(error2);
        }
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return generateErrorMessage("Failed to change table " + this.tableName);
        }
        return getQueryResults(null);
    }

}
