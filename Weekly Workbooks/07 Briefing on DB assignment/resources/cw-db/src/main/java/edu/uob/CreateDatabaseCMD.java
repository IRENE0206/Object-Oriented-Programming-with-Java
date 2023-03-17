package edu.uob;

public class CreateDatabaseCMD extends CreateCMD {
    public CreateDatabaseCMD(String databaseName) {
        super();
        this.databaseName = databaseName;
    }

    @Override
    public String query(DBServer dbServer) {
        return okTag;
    }
}
