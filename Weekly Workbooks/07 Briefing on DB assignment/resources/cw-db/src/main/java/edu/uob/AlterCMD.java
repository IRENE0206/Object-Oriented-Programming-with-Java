package edu.uob;

public abstract class AlterCMD extends DBCmd {
    String alterType;
    String attributeName;

    public AlterCMD(String tableName, String alterType, String attributeName) {
        this.commandType = "ALTER";
        this.tableName = tableName;
        this.alterType = alterType;
        this.dbTable = new DBTable(this.tableName);
        this.attributeName = attributeName;
    }

    public abstract String query(DBServer dbServer);
}

