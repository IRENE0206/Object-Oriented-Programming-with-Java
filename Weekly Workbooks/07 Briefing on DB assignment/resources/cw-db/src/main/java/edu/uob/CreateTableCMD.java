package edu.uob;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CreateTableCMD extends CreateCMD {

    public CreateTableCMD(String tableName) {
        super();
        this.tableName = tableName;
        this.colNames = new ArrayList<>();
    }

    public CreateTableCMD(String tableName, List<String> colNames) {
        this.tableName = tableName;
        this.colNames = colNames;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        String tabFileToCreate = this.databasePath + File.separator + this.tableName + tabTileExtension;
        File fileToCreate = new File(tabFileToCreate);
        if (fileToCreate.exists()) {
            return errorMessage("Table " + this.tableName + " already exists");
        }
        DBTable dbTable;
        if (this.colNames.size() > 0) {
            dbTable = new DBTable(this.tableName, this.colNames);
        } else {
            dbTable = new DBTable(this.tableName);
        }
        return dbTable.toFile(this.databasePath);
    }
}
