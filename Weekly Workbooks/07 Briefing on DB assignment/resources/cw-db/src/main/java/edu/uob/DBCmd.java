package edu.uob;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public abstract class DBCmd {
    protected String errorTag = "[ERROR] ";
    protected String okTag = "[OK]\n";
    protected String errorMessage;
    protected String tabFileExtension = ".tab";
    protected String databaseName;
    protected String databasePath;
    protected String tableName;
    protected String tableFilePath;
    protected List<String> attributeList;
    protected List<String> tableNames;
    protected List<Condition> conditions;
    protected DBTable dbTable;

    protected boolean interpretError;

    public abstract String query(DBServer dbServer);

    protected void setDatabasePathFromStorageFolderPath(String storageFolderPath) {
        this.databasePath = storageFolderPath + File.separator + this.databaseName.toLowerCase();
    }

    protected void setDatabasePathFromCurrentDatabasePath(String currentDatabasePath) {
        this.databasePath = currentDatabasePath;
    }

    public String generateErrorMessage(String message) {
        this.interpretError = true;
        return errorTag + message;
    }

    public void setErrorMessage(String message) {
        this.interpretError = true;
        this.errorMessage = errorTag + message;
    }

    protected String getQueryResults(String results) {
        if (results != null) {
            return okTag + results;
        }
        return okTag;
    }

    protected void setTableFilePath() {
        this.tableFilePath = getTableFilePath(this.tableName.toLowerCase());
    }

    protected String getTableFilePath(String tbName) {
        return this.databasePath + File.separator + tbName.toLowerCase() + tabFileExtension;
    }

    protected String loadTabFileToDBTable(String tbFilePath, DBTable table) {
        File fileToRead = new File(tbFilePath);
        FileReader reader;
        try {
            reader = new FileReader(fileToRead);
        } catch (FileNotFoundException e) {
            return generateErrorMessage("Table " + table.getTableName() + " not found");
        }
        BufferedReader buffReader = new BufferedReader(reader);
        try {
            String firstLine = buffReader.readLine();
            if (firstLine.length() > 0) {
                String[] firstLineSplit = firstLine.split("\t");
                table.setColNamesNoNeedToAddId(Arrays.stream(firstLineSplit).toList());
                buffReader.
                        lines().
                        filter(s -> s.length() > 0).
                        forEachOrdered(s -> table.setRows(new LinkedList<>(Arrays.asList(s.split("\t")))));
            }
            buffReader.close();
            reader.close();
            return null;
        } catch (IOException e) {
            return generateErrorMessage("Failed to read table " + tbFilePath);
        }
    }

    protected boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public boolean evaluateConditions(List<String> row, List<String> colNames) {
        Stack<Boolean> booleans = new Stack<>();
        for (Condition condition : conditions) {
            if (condition.isBoolOperator()) {
                String boolOperator = condition.getOperator();
                boolean bool1 = booleans.pop();
                boolean bool2 = booleans.pop();
                if (stringsEqualCaseInsensitively(boolOperator, "OR")) {
                    if (bool1) {
                        booleans.push(true);
                    } else {
                        booleans.push(bool2);
                    }
                } else {
                    if (!bool1) {
                        booleans.push(false);
                    } else {
                        booleans.push(bool2);
                    }
                }
            } else {
                condition.setColNames(colNames);
                condition.setResult(tableName, row);
                String error = condition.getErrorMessage();
                if (error != null) {
                    this.interpretError = true;
                    this.setErrorMessage(error);
                    return false;
                }
                booleans.push(condition.getResult());
            }
        }
        return booleans.pop();
    }

    public boolean hasInterpretError() {
        return this.interpretError;
    }
}

