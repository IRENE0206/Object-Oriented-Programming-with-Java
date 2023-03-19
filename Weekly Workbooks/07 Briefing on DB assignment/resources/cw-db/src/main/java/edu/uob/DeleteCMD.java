package edu.uob;

import java.util.List;
import java.util.concurrent.locks.Condition;

public class DeleteCMD extends DBCmd {

    public DeleteCMD() {
        this.commandType = "DELETE";
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setConditions(List<Condition> conditions) {

    }

    @Override
    public String query(DBServer dbServer) {
        return null;
    }
}
