package edu.uob;

import java.util.List;

public abstract class Condition {
    String errorMessage;
    List<String> columnNames;
    String operator;
    boolean result;

    abstract boolean evaluate(List<String> row);
    public abstract void setCoNames(List<String> colNames);
    public abstract void setResult(List<String> row);
    public abstract boolean getResult();
    public String getOperator() {
        return this.operator;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public abstract boolean isBoolOperator();


}

