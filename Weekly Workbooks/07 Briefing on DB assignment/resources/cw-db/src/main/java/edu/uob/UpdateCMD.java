package edu.uob;

import java.util.List;
import java.util.Map;

public class UpdateCMD extends DBCmd {
    private Map<String, String> nameValueList;

    public UpdateCMD(String tableName, Map<String, String> nameValueList, List<Condition> conditions) {
        this.tableName = tableName;
        this.nameValueList = nameValueList;
        this.conditions = conditions;
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
        if (!this.dbTable.update(this.nameValueList, this)) {
            return this.errorMessage;
        }
        if (this.dbTable.failToFile(this.tableFilePath)) {
            return generateErrorMessage("Failed to update " + this.tableName);
        }
        return getQueryResults(null);
    }
}
