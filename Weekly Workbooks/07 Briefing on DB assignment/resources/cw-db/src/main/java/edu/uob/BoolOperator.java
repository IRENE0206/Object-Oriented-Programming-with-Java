package edu.uob;

import java.util.List;

public class BoolOperator extends Condition {
    private String boolOperator;
    private Condition left;
    private Condition right;

    public BoolOperator(String boolOperator) {
        this.boolOperator = boolOperator;
    }

    @Override
    public boolean getResult(List<String> row) {
        boolean leftResult = left.getResult(row);
        String error1 = left.getErrorMessage();
        if (!error1.isEmpty()) {
            setErrorMessage(error1);
            return false;
        }
        if (stringsEqualCaseInsensitively(this.boolOperator, "AND")) {
            if (!leftResult) {
                return false;
            }
            boolean rightResult = right.getResult(row);
            String error2 = right.getErrorMessage();
            if (!error2.isEmpty()) {
                setErrorMessage(error2);
                return false;
            }
            return rightResult;
        } else if (leftResult) {
            return true;
        }
        boolean rightResult = right.getResult(row);
        String error2 = right.getErrorMessage();
        if (!error2.isEmpty()) {
            setErrorMessage(error2);
            return false;
        }
        return rightResult;
    }

    @Override
    public void setCoNames(List<String> colNames) {
        this.left.setCoNames(colNames);
        this.right.setCoNames(colNames);
    }
    @Override
    void setErrorMessage(String message) {
        this.errorMessage = message;
    }
}
