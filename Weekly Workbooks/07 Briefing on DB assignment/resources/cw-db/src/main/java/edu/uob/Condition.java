package edu.uob;

import java.util.List;

public abstract class Condition {
    protected String errorMessage;
    protected List<String> columnNames;
    protected String operator;
    protected boolean result;

    public abstract void setColNames(List<String> colNames);
    public abstract void setResult(String tableName, List<String> row);
    public abstract boolean getResult();
    public String getOperator() {
        return this.operator;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    protected boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public abstract boolean isBoolOperator();

}

