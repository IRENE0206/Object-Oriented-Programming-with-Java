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
        String error1 = tableFileToDBTable(this.tableFilePath, this.dbTable);
        if (!error1.isEmpty()) {
            // System.out.println("ERROR1");
            return error1;
        }
        if (this.dbTable.isEmpty()) {
            this.dbTable.setColNames(this.attributeList);
            if (this.dbTable.failToFile(this.tableFilePath)) {
                return errorMessage("Failed to save changes to table file");
            }
            return getQueryResults("");
        }
        String error2 = this.dbTable.addRow(this.attributeList);
        if (!error2.isEmpty()) {
            // System.out.println("ERROR2");
            return errorMessage(error2);
        }
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return errorMessage("Failed to save changes to table file");
        }
        return getQueryResults("");
    }
}
