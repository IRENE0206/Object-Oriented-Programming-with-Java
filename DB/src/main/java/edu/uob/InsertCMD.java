package edu.uob;

import java.util.List;

public class InsertCMD extends DBCmd {
    public InsertCMD(String tableName, List<String> attributeList) {
        this.tableName = tableName;
        this.attributeList = attributeList;
        this.dbTable = new DBTable(this.tableName);
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = loadTabFileToDBTable(this.tableFilePath, this.dbTable);
        if (error1 != null) {
            return error1;
        }
        String error2 = this.dbTable.addRow(this.attributeList);
        if (error2 != null) {
            return generateErrorMessage(error2);
        }
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return generateErrorMessage("Failed to save changes to table file");
        }
        return getQueryResults(null);
    }
}
