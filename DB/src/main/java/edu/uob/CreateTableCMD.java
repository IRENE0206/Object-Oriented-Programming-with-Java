package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateTableCMD extends DBCmd {

    public CreateTableCMD(String tableName) {
        super();
        this.tableName = tableName.toLowerCase();
        this.attributeList = new ArrayList<>();
    }

    public CreateTableCMD(String tableName, List<String> attributeList) {
        this.tableName = tableName;
        this.attributeList = attributeList;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        File fileToCreate = new File(this.tableFilePath);
        if (fileToCreate.exists()) {
            return generateErrorMessage("Table " + this.tableName + " already exists");
        }
        DBTable dbTable = new DBTable(this.tableName);
        if (this.attributeList.size() > 0) {
            String error = dbTable.setColNames(attributeList, false);
            if (error != null) {
                return generateErrorMessage(error);
            }
        }
        if (dbTable.failToFile(this.tableFilePath)) {
            return generateErrorMessage("Failed to create table " + this.tableName);
        }
        return getQueryResults(null);
    }
}
