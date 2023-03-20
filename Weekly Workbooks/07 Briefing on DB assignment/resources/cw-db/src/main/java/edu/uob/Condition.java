package edu.uob;

import java.util.List;

public abstract class Condition {
    String errorMessage;
    List<String> colNames;

    public abstract boolean getResult(List<String> row);
    public abstract void setCoNames(List<String> colNames);

    public String getErrorMessage() {
        return this.errorMessage;
    }

    boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }
    abstract void setErrorMessage(String message);
}

