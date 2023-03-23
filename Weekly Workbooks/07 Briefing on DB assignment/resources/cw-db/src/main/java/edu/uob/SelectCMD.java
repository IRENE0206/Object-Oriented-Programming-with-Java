package edu.uob;

import java.util.List;

public class SelectCMD extends DBCmd {
    private final boolean selectAll;
    private final boolean unconditional;

    public SelectCMD(String tableName) {
        this.tableName = tableName;
        this.selectAll = true;
        this.unconditional = true;
        this.dbTable = new DBTable(this.tableName);
    }

    public SelectCMD(String tableName, List<Condition> conditions, boolean all) {
        this.tableName = tableName;
        this.selectAll = all;
        this.unconditional = false;
        this.conditions = conditions;
        this.dbTable = new DBTable(this.tableName);
    }

    public SelectCMD(String tableName, List<String> attributeList) {
        this.tableName = tableName;
        this.selectAll = false;
        this.unconditional = true;
        this.dbTable = new DBTable(this.tableName);
        this.attributeList = attributeList;
    }

    public SelectCMD(String tableName, List<String> attributeList, List<Condition> conditions) {
        this.tableName = tableName;
        this.selectAll = false;
        this.unconditional = false;
        this.dbTable = new DBTable(this.tableName);
        this.attributeList = attributeList;
        this.conditions = conditions;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = loadTabFileToDBTable(this.tableFilePath, this.dbTable);
        if (error1 != null) {
            return error1;
        }
        if (this.selectAll && this.unconditional) {
            return getQueryResults(this.dbTable.toString());
        } else if (!this.selectAll && !this.dbTable.containsQueriedAttributes(this.attributeList)) {
            return generateErrorMessage("Invalid attribute list for " + tableName);
        } else if (!this.selectAll && this.unconditional) {
            String contents = this.dbTable.selectedContentToString(this.attributeList);
            return interpretErrorOrResults(contents);
        } else if (this.selectAll) {
            String contents = this.dbTable.selectedContentToString(this);
            return interpretErrorOrResults(contents);
        }
        String contents = this.dbTable.selectedContentToString(this.attributeList, this);
        return interpretErrorOrResults(contents);
    }

    private String interpretErrorOrResults(String contents) {
        if (this.hasInterpretError()) {
            return this.errorMessage;
        }
        return getQueryResults(contents);
    }
}
