package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateTableCMD extends CreateCMD {

    public CreateTableCMD(String tableName) {
        super();
        this.tableName = tableName;
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
            return errorMessage("Table " + this.tableName + " already exists");
        }
        DBTable dbTable = new DBTable(this.tableName);
        if (this.attributeList.size() > 0) {
            dbTable.setColNames(attributeList);
        }
        System.out.println(this.tableFilePath);
        if (dbTable.failToFile(this.tableFilePath)) {
            return errorMessage("Failed to create table " + this.tableName);
        }
        return getQueryResults("");
    }
}
