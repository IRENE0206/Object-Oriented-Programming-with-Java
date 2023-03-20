package edu.uob;

import java.util.List;

public class DeleteCMD extends DBCmd {

    public DeleteCMD() {
        this.commandType = "DELETE";
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String query(DBServer dbServer) {
        return null;
    }
}
