package edu.uob;

import java.util.List;

public class BoolOperator extends Condition {

    public BoolOperator(String boolOperator) {
        this.operator = boolOperator;
    }

    @Override
    boolean evaluate(List<String> row) {
        return false;
    }

    @Override
    public void setCoNames(List<String> colNames) {
        return;
    }

    @Override
    public void setResult(List<String> row) {
        this.result = evaluate(row);
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
