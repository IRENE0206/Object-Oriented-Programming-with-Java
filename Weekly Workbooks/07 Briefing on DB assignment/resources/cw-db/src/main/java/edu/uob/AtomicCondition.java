package edu.uob;

import java.util.List;

public class AtomicCondition extends Condition {
    private String comparator;
    private String attributeName;
    private int attributeIndex;
    private String value;

    public AtomicCondition(String comparator, String attributeName, String value) {
        this.comparator = comparator;
        this.attributeName = attributeName;
        this.value = value;
        this.errorMessage = "";
    }

    public void setAttributeIndex() {
        for (int i = 0; i < this.colNames.size(); i++) {
            if (stringsEqualCaseInsensitively(this.colNames.get(i), this.attributeName)) {
                this.attributeIndex = i;
                return;
            }
        }
        setErrorMessage(" does not exist in the table");
    }

    @Override
    public boolean getResult(List<String> row) {
        setAttributeIndex();
        if (this.errorMessage.isEmpty()) {
            String attribute = row.get(this.attributeIndex);
            if (isBooleanLiteral(attribute)) {
                return boolValueCompare(attribute);
            } else if (isNull(attribute)) {
                return nullValueCompare();
            } else {
                return stringValueCompare(attribute);
            }
        }
        return false;
    }

    @Override
    public void setCoNames(List<String> colNames) {
        this.colNames = colNames;
    }

    @Override
    void setErrorMessage(String message) {
        this.errorMessage = attributeName + message;
    }

    private boolean nullValueCompare() {
        if (equalSignComparator()) {
            return isNull(this.value);
        } else if (unequalSignComparator()) {
            return !isNull(this.value);
        }
        return false;
    }

    private boolean stringValueCompare(String s) {
        int compareResult = s.toLowerCase().compareTo(this.value.toLowerCase());
        if (isBooleanLiteral(this.value)) {
            return false;
        } else if (isNull(this.value)) {
            return compareResult == 0;
        }else if (equalSignComparator()) {
            return compareResult == 0;
        } else if (unequalSignComparator()) {
            return compareResult != 0;
        } else if (biggerThanComparator()) {
            return compareResult > 0;
        } else if (biggerThanOrEqualComparator()) {
            return compareResult >= 0;
        } else if (smallerThanComparator()) {
            return compareResult < 0;
        } else if (smallerThanOrEqualComparator()) {
            return compareResult <= 0;
        }
        return s.contains(this.value);
    }

    private boolean boolValueCompare(String boolValue) {
        if (equalSignComparator()) {
            return stringsEqualCaseInsensitively(this.value, boolValue);
        } else if (unequalSignComparator()) {
            return !stringsEqualCaseInsensitively(this.value, boolValue);
        }
        return false;
    }

    private boolean isBooleanLiteral(String s) {
        return stringsEqualCaseInsensitively(s, "TRUE") || stringsEqualCaseInsensitively(s, "FALSE");
    }

    private boolean isNull(String s) {
        return stringsEqualCaseInsensitively(s, "NULL");
    }

    private boolean equalSignComparator() {
        return this.comparator.compareTo("==") == 0;
    }

    private boolean unequalSignComparator() {
        return this.comparator.compareTo("!=") == 0;
    }

    private boolean biggerThanComparator() {
        return this.comparator.compareTo(">") == 0;
    }

    private boolean biggerThanOrEqualComparator() {
        return this.comparator.compareTo(">=") == 0;
    }

    private boolean smallerThanComparator() {
        return this.comparator.compareTo("<") == 0;
    }

    private boolean smallerThanOrEqualComparator() {
        return this.comparator.compareTo("<=") == 0;
    }
}
