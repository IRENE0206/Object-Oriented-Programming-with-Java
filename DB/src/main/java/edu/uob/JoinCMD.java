package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class JoinCMD extends DBCmd {

    public JoinCMD(String tableName1, String tableName2, String attributeName1, String attributeName2) {
        this.tableNames = new ArrayList<>();
        tableNames.add(tableName1);
        tableNames.add(tableName2);
        this.attributeList = new ArrayList<>();
        attributeList.add(attributeName1);
        attributeList.add(attributeName2);
    }

    @Override
    public String query(DBServer dbServer) {
        setDatabasePathFromCurrentDatabasePath(dbServer.getDatabasePath());
        String tableName1 = tableNames.get(0);
        String tableFilePath1 = getTableFilePath(tableName1);
        DBTable dbTable1 = new DBTable(tableName1);
        String error1 = loadTabFileToDBTable(tableFilePath1, dbTable1);
        if (error1 != null) {
            return error1;
        }
        String tableName2 = tableNames.get(1);
        String tableFilePath2 = getTableFilePath(tableName2);
        DBTable dbTable2 = new DBTable(tableName2);
        String error2 = loadTabFileToDBTable(tableFilePath2, dbTable2);
        if (error2 != null) {
            return error2;
        }
        List<String> attributeList1 = dbTable1.getColNames();
        String attribute1 = this.attributeList.get(0);
        if (!dbTable1.containsAttribute(attribute1)) {
            return generateErrorMessage("Table " + tableName1 + " does not have attribute " + attribute1);
        }
        int index1 = dbTable1.getIndexOfAttribute(attribute1);
        List<String> attributeList2 = dbTable2.getColNames();
        String attribute2 = this.attributeList.get(1);
        if (!dbTable2.containsAttribute(attribute2)) {
            return generateErrorMessage("Table " + tableName2 + " does not have attribute " + attribute2);
        }
        int index2 = dbTable2.getIndexOfAttribute(attribute2);
        DBTable tmp = new DBTable("tmp");
        List<String> newAttributeList = new ArrayList<>(addColNames(attributeList1, tableName1, index1));
        newAttributeList.addAll(addColNames(attributeList2, tableName2, index2));
        String error3 = tmp.setColNames(newAttributeList, true);
        if (error3 != null) {
            return generateErrorMessage(error3);
        }
        for (List<String> row : dbTable1.getRows()) {
            List<String> matchedRow = getMatchedRowFromTableRows(row.get(index1), dbTable2.getRows(), index2);
            if (matchedRow != null) {
                List<String> newRow = new ArrayList<>(rowGetRidOfIdAndKey(row, index1));
                newRow.addAll(matchedRow);
                tmp.addRow(newRow);
            }
        }
        return getQueryResults(tmp.toString());
    }

    private List<String> addColNames(List<String> sourceList, String sourceTableName, int keyIndex) {
        List<String> result = new ArrayList<>();
        for (int i = 1; i < sourceList.size(); i++) {
            if (i != keyIndex) {
                result.add(sourceTableName + "." + sourceList.get(i));
            }
        }
        return result;
    }

    private List<String> getMatchedRowFromTableRows(String keyValue, List<List<String>> rows, int keyIndex) {
        for (List<String> row : rows) {
            if (stringsEqualCaseInsensitively(row.get(keyIndex), keyValue)) {
                return rowGetRidOfIdAndKey(row, keyIndex);
            }
        }
        return null;
    }

    private List<String> rowGetRidOfIdAndKey(List<String> sourceList, int keyIndex) {
        if (keyIndex == 0) {
            return sourceList.subList(1, sourceList.size());
        }
        List<String> result = new ArrayList<>(sourceList.subList(1, keyIndex));
        result.addAll(sourceList.subList(keyIndex + 1, sourceList.size()));
        return result;
    }
}
