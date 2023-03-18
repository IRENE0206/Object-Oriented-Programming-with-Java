package edu.uob;

import java.util.List;

public class InsertCMD extends DBCmd {
    public InsertCMD(String tableName, List<String> values) {
        this.tableName = tableName;
        this.values = values;
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        setTableFilePath();
        String error1 = tableFileToDBTable();
        if (!error1.isEmpty()) {
            return error1;
        }
        String error2 = this.dbTable.addRow(this.values);
        if (!error2.isEmpty()) {
            return error2;
        }
        return getQueryResults("");
    }
}
