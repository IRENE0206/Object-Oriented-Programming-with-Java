package edu.uob;

import java.util.List;

public class DeleteCMD extends DBCmd {

    public DeleteCMD(String tableName, List<Condition> conditions) {
        this.commandType = "DELETE";
        this.tableName = tableName;
        this.conditions = conditions;
        this.dbTable = new DBTable(tableName);
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = tableFileToDBTable(this.tableFilePath, this.dbTable);
        if (!error1.isEmpty()) {
            return error1;
        }
        if (this.dbTable.deleteRow(this.conditions, this)) {
            if (this.dbTable.failToFile(this.tableFilePath)) {
                return errorMessage("Failed to update " + this.tableName);
            }
            return getQueryResults("");
        }
        return this.errorTag;
    }
}
