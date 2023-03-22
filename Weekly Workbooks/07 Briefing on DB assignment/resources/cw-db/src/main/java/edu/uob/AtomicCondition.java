package edu.uob;

import java.util.Iterator;
import java.util.List;

public class AtomicCondition extends Condition {
    private String attributeName;
    private int attributeIndex;
    private String value;
    private boolean isLiteralValue;

    public AtomicCondition(String comparator, String attributeName, String value, boolean isLiteralValue) {
        this.operator = comparator;
        this.attributeName = attributeName;
        this.value = value;
        this.errorMessage = null;
        this.isLiteralValue = isLiteralValue;
    }

    public void setAttributeIndex(String tableName) {
        for (int i = 0; i < this.columnNames.size(); i++) {
            String columnName = this.columnNames.get(i);
            if (columnName.contains(".")) {
                int index1 = columnName.indexOf(".");
                String tbName1 = columnName.substring(0, index1);
                if (!stringsEqualCaseInsensitively(tableName, tbName1)) {
                    this.errorMessage = columnName + " does not exist in the table " + tableName;
                    return;
                }
                columnName = columnName.substring(index1 + 1);
            }
            String attrName = this.attributeName;
            if (attrName.contains(".")) {
                int index2 = attrName.indexOf(".");
                String tbName2 = attrName.substring(0, index2);
                if (!stringsEqualCaseInsensitively(tableName, tbName2)) {
                    this.errorMessage = attrName + " does not exist in the table " + tableName;
                    return;
                }
                attrName = attrName.substring(index2 + 1);
            }
            if (stringsEqualCaseInsensitively(columnName, attrName)) {
                this.attributeIndex = i;
                return;
            }
        }
        this.errorMessage = this.attributeName + " does not exist in the table " + tableName;
    }

    @Override
    boolean evaluate(String tableName, List<String> row) {
        setAttributeIndex(tableName);
        if (this.errorMessage == null) {
            String attribute = row.get(this.attributeIndex);
            if (likeCompare()) {
                return stringValueCompare(attribute);
            } else if (smallerThanComparator() || smallerThanOrEqualComparator()
                    || biggerThanOrEqualComparator() || biggerThanComparator()) {
                if (isIntegerLiteral(attribute)) {
                    return integerLiteralCompare(attribute);
                } else if (isFloatLiteral(attribute)) {
                    return floatLiteralCompare(attribute);
                }
            } else if (isBooleanLiteral(attribute)) {
                return boolValueCompare(attribute);
            } else if (isNull(attribute)) {
                return nullValueCompare();
            }
            return stringValueCompare(attribute);
        }
        return false;
    }

    @Override
    public void setColNames(List<String> colNames) {
        this.columnNames = colNames;
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
        return false;
    }

    private boolean nullValueCompare() {
        if (equalSignComparator()) {
            return isNull(this.value);
        } else if (unequalSignComparator()) {
            return !isNull(this.value);
        }
        return false;
    }

    private boolean integerLiteralCompare(String s) {
        int i = Integer.parseInt(s);
        if (isIntegerLiteral(this.value)) {
            int i2 = Integer.parseInt(this.value);
            if (equalSignComparator()) {
                return i == i2;
            } else if (unequalSignComparator()) {
                return i != i2;
            } else if (biggerThanComparator()) {
                return i > i2;
            } else if (biggerThanOrEqualComparator()) {
                return i >= i2;
            } else if (smallerThanComparator()) {
                return i < i2;
            } else if (smallerThanOrEqualComparator()) {
                return i <= i2;
            }
        } else if (isFloatLiteral(this.value)) {
            float f2 = Float.parseFloat(this.value);
            if (equalSignComparator()) {
                return i == f2;
            } else if (unequalSignComparator()) {
                return i != f2;
            } else if (biggerThanComparator()) {
                return i > f2;
            } else if (biggerThanOrEqualComparator()) {
                return i >= f2;
            } else if (smallerThanComparator()) {
                return i < f2;
            } else if (smallerThanOrEqualComparator()) {
                return i <= f2;
            }
        }
        return false;
    }

    private boolean floatLiteralCompare(String s) {
        float f = Float.parseFloat(s);
        if (isIntegerLiteral(this.value)) {
            int i2 = Integer.parseInt(this.value);
            if (equalSignComparator()) {
                return f == i2;
            } else if (unequalSignComparator()) {
                return f != i2;
            } else if (biggerThanComparator()) {
                return f > i2;
            } else if (biggerThanOrEqualComparator()) {
                return f >= i2;
            } else if (smallerThanComparator()) {
                return f < i2;
            } else if (smallerThanOrEqualComparator()) {
                return f <= i2;
            }
        } else if (isFloatLiteral(this.value)) {
            float f2 = Float.parseFloat(this.value);
            if (equalSignComparator()) {
                return f == f2;
            } else if (unequalSignComparator()) {
                return f != f2;
            } else if (biggerThanComparator()) {
                return f > f2;
            } else if (biggerThanOrEqualComparator()) {
                return f >= f2;
            } else if (smallerThanComparator()) {
                return f < f2;
            } else if (smallerThanOrEqualComparator()) {
                return f <= f2;
            }
        }
        return false;
    }

    private boolean stringValueCompare(String s) {
        int compareResult;
        if (this.isLiteralValue) {
            compareResult = s.toLowerCase().compareTo(this.value);
        } else {
            compareResult = s.toLowerCase().compareTo(this.value.toLowerCase());
        }
        if (equalSignComparator()) {
            return compareResult == 0;
        } else if (unequalSignComparator()) {
            return compareResult != 0;
        } else if (this.isLiteralValue && likeCompare()) {
            return s.contains(this.value);
        } else if (likeCompare()){
            return s.toLowerCase().contains(this.value.toLowerCase());
        }
        return false;
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

    private boolean isIntegerLiteral(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' > c || c > '9') && (i == 0 && c != '-' && c != '+')) {
                return false;
            }
        }
        return true;
    }

    private boolean isFloatLiteral(String s) {
        if (!s.contains(".")) {
            return false;
        }
        int index = s.indexOf('.');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' > c || c > '9') && (i == 0 && c != '-' && c != '+') && i != index) {
                return false;
            }
        }
        return true;
    }

    private boolean equalSignComparator() {
        return this.operator.compareTo("==") == 0;
    }

    private boolean likeCompare() {
        return this.operator.toUpperCase().compareTo("LIKE") == 0;
    }

    private boolean unequalSignComparator() {
        return this.operator.compareTo("!=") == 0;
    }

    private boolean biggerThanComparator() {
        return this.operator.compareTo(">") == 0;
    }

    private boolean biggerThanOrEqualComparator() {
        return this.operator.compareTo(">=") == 0;
    }

    private boolean smallerThanComparator() {
        return this.operator.compareTo("<") == 0;
    }

    private boolean smallerThanOrEqualComparator() {
        return this.operator.compareTo("<=") == 0;
    }

}
