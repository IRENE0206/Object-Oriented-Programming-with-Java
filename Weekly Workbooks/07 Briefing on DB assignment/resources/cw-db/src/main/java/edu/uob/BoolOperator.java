package edu.uob;

import java.util.List;

public class BoolOperator extends Condition {

    public BoolOperator(String boolOperator) {
        this.operator = boolOperator;
    }

    @Override
    boolean evaluate(String tableName, List<String> row) {
        return false;
    }

    @Override
    public void setColNames(List<String> colNames) {
        return;
    }

    @Override
    public void setResult(String tableName, List<String> row) {
        this.result = evaluate(tableName, row);
    }

    @Override
    public boolean getResult() {
        return this.result;
    }

    @Override
    public boolean isBoolOperator() {
        return true;
    }
}
