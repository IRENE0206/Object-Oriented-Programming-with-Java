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

    public SelectCMD(String tableName, List<Condition> conditions, boolean selectAll) {
        this.tableName = tableName;
        this.selectAll = selectAll;
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
        String error1 = tableFileToDBTable(this.tableFilePath, this.dbTable);
        System.out.println("0HEY");
        if (!error1.isEmpty()) {
            return error1;
        }
        if (this.selectAll && this.unconditional) {
            System.out.println("1HEY");
            return getQueryResults(this.dbTable.toString());
        } else if (!this.selectAll && !this.dbTable.containsQueriedAttributes(this.attributeList)) {
            System.out.println("2HEY");
            return errorMessage("Invalid attribute list for " + tableName);
        } else if (!this.selectAll && this.unconditional) {
            System.out.println("3HEY");
            return this.dbTable.selectedContentToString(this.attributeList);
        } else if (this.selectAll && !this.unconditional) {
            System.out.println("4HEY");
            return this.dbTable.selectedContentToString(this);
        } else {
            System.out.println("5HEY");
            return this.dbTable.selectedContentToString(this.attributeList, this);
        }
    }
}
