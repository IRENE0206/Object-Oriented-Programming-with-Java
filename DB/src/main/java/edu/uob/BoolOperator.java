package edu.uob;

import java.util.List;

public class BoolOperator extends Condition {

    public BoolOperator(String boolOperator) {
        this.operator = boolOperator;
    }

    @Override
    public void setColNames(List<String> colNames) {
        this.columnNames = null;
    }

    @Override
    public void setResult(String tableName, List<String> row) {
        this.result = false;
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
