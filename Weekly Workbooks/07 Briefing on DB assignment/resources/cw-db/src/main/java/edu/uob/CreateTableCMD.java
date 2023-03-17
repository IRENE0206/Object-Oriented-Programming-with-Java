package edu.uob;

import java.util.List;

public class CreateTableCMD extends CreateCMD {

    public CreateTableCMD(String tableName) {
        this.tableName = tableName;
    }

    public CreateTableCMD(String tableName, List<String> tableNameList, List<String> attributeList) {
        this.tableName = tableName;
        this.tableNames = tableNameList;
        this.attributeList = attributeList;
    }

    @Override
    public String query(DBServer dbServer) {
        return okTag;
    }
}
