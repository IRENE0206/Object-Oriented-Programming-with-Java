package edu.uob;

public class UseCMD extends DBCmd {
    public UseCMD(String databaseName) {
        this.commandType = "USE";
        this.databaseName = databaseName;
    }

    @Override
    public String query(DBServer dbServer) {
        return okTag;
    }
}
