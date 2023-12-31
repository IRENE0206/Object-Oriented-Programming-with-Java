package edu.uob;

import java.util.List;

public class DeleteCMD extends DBCmd {

    public DeleteCMD(String tableName, List<Condition> conditions) {
        this.tableName = tableName;
        this.conditions = conditions;
        this.dbTable = new DBTable(tableName);
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = loadTabFileToDBTable(this.tableFilePath, this.dbTable);
        if (error1 != null) {
            return error1;
        }
        if (this.dbTable.deleteRow(this)) {
            if (this.dbTable.failToFile(this.tableFilePath)) {
                return generateErrorMessage("Failed to update " + this.tableName);
            }
            return getQueryResults(null);
        }
        return this.errorTag;
    }
}
