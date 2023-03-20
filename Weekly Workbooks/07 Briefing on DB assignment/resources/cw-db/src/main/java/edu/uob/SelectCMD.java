package edu.uob;

import java.util.List;

public class SelectCMD extends DBCmd {
    boolean selectAll;
    boolean unconditional;

    public SelectCMD(String tableName) {
        this.tableName = tableName;
        this.selectAll = true;
        this.unconditional = true;
        this.dbTable = new DBTable(this.tableName);
    }

    public SelectCMD(String tableName, Condition condition, boolean unconditional) {
        this.tableName = tableName;
        this.unconditional = unconditional;
        this.condition = condition;
        this.dbTable = new DBTable(this.tableName);
    }

    public SelectCMD(String tableName, List<String> attributeList) {
        this.tableName = tableName;
        this.selectAll = false;
        this.unconditional = true;
        this.dbTable = new DBTable(this.tableName);
        this.attributeList = attributeList;
    }

    public SelectCMD(String tableName, List<String> attributeList, Condition condition) {
        this.tableName = tableName;
        this.selectAll = false;
        this.unconditional = false;
        this.dbTable = new DBTable(this.tableName);
        this.attributeList = attributeList;
        this.condition = condition;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = tableFileToDBTable(this.tableFilePath, this.dbTable);
        if (!error1.isEmpty()) {
            return error1;
        }
        if (this.selectAll && this.unconditional) {
            return getQueryResults(this.dbTable.toString());
        } else if (!this.dbTable.containsQueriedAttributes(this.attributeList)) {
            return errorMessage("Invalid attribute list for " + tableName);
        } else if (!this.selectAll && this.unconditional) {
            return this.dbTable.selectedContentToString(this.attributeList);
        } else if (this.selectAll && !this.unconditional) {
            // TODO
            return "";
        } else {
            // TODO
            return "";
        }
    }
}
