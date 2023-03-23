package edu.uob;

import java.util.List;

public class AtomicCondition extends Condition {
    private final String attributeName;
    private int attributeIndex;
    private final String value;
    private final boolean isLiteralValue;

    public AtomicCondition(String comparator, String attrName, String val, boolean isLiteral) {
        this.operator = comparator;
        this.attributeName = attrName;
        this.value = val;
        this.errorMessage = null;
        this.isLiteralValue = isLiteral;
    }

    public void setAttributeIndex(String tableName) {
        for (int i = 0; i < this.columnNames.size(); i++) {
            String columnName = this.columnNames.get(i);
            if (columnName.contains(".")) {
                int index1 = columnName.indexOf('.');
                String tbName1 = columnName.substring(0, index1);
                if (!stringsEqualCaseInsensitively(tableName, tbName1)) {
                    this.errorMessage = columnName + " does not exist in the table " + tableName;
                    return;
                }
                columnName = columnName.substring(index1 + 1);
            }
            String attrName = this.attributeName;
            if (attrName.contains(".")) {
                int index2 = attrName.indexOf('.');
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

    private boolean evaluate(String tableName, List<String> row) {
        setAttributeIndex(tableName);
        if (this.errorMessage == null) {
            String attribute = row.get(this.attributeIndex);
            if (isLikeCompare()) {
                return stringValueCompare(attribute);
            } else if (isSmallerThanComparator() || isSmallerThanOrEqualComparator()
                    || isBiggerThanOrEqualComparator() || isBiggerThanComparator()) {
                if (isIntegerLiteral(attribute)) {
                    return integerLiteralCompare(attribute);
                } else if (isFloatLiteral(attribute)) {
                    return floatLiteralCompare(attribute);
                }
            } else if (isBooleanLiteral(attribute)) {
                return isBoolValueCompare(attribute);
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
        if (isEqualSignComparator()) {
            return isNull(this.value);
        } else if (isUnequalSignComparator()) {
            return !isNull(this.value);
        }
        return false;
    }

    private boolean integerLiteralCompare(String s) {
        int i = Integer.parseInt(s);
        if (isIntegerLiteral(this.value)) {
            return intComparesInt(i, Integer.parseInt(this.value));
        } else if (isFloatLiteral(this.value)) {
            return intComparesFloat(i, Float.parseFloat(this.value));
        }
        return false;
    }

    private boolean intComparesInt(int i1, int i2) {
        if (isEqualSignComparator()) {
            return i1 == i2;
        } else if (isUnequalSignComparator()) {
            return i1 != i2;
        } else if (isBiggerThanComparator()) {
            return i1 > i2;
        } else if (isBiggerThanOrEqualComparator()) {
            return i1 >= i2;
        } else if (isSmallerThanComparator()) {
            return i1 < i2;
        } else if (isSmallerThanOrEqualComparator()) {
            return i1 <= i2;
        }
        return false;
    }

    private boolean intComparesFloat(int i, float f) {
        if (isEqualSignComparator()) {
            return i == f;
        } else if (isUnequalSignComparator()) {
            return i != f;
        } else if (isBiggerThanComparator()) {
            return i > f;
        } else if (isBiggerThanOrEqualComparator()) {
            return i >= f;
        } else if (isSmallerThanComparator()) {
            return i < f;
        } else if (isSmallerThanOrEqualComparator()) {
            return i <= f;
        }
        return false;
    }

    private boolean floatLiteralCompare(String s) {
        float f = Float.parseFloat(s);
        if (isIntegerLiteral(this.value)) {
            return floatComparesInteger(f, Integer.parseInt(this.value));
        } else if (isFloatLiteral(this.value)) {
            return floatCompareFloat(f, Float.parseFloat(this.value));
        }
        return false;
    }

    private boolean floatComparesInteger(float f, int i) {
        if (isEqualSignComparator()) {
            return f == i;
        } else if (isUnequalSignComparator()) {
            return f != i;
        } else if (isBiggerThanComparator()) {
            return f > i;
        } else if (isBiggerThanOrEqualComparator()) {
            return f >= i;
        } else if (isSmallerThanComparator()) {
            return f < i;
        } else if (isSmallerThanOrEqualComparator()) {
            return f <= i;
        }
        return false;
    }

    private boolean floatCompareFloat(float f1, float f2) {
        if (isEqualSignComparator()) {
            return f1 == f2;
        } else if (isUnequalSignComparator()) {
            return f1 != f2;
        } else if (isBiggerThanComparator()) {
            return f1 > f2;
        } else if (isBiggerThanOrEqualComparator()) {
            return f1 >= f2;
        } else if (isSmallerThanComparator()) {
            return f1 < f2;
        } else if (isSmallerThanOrEqualComparator()) {
            return f1 <= f2;
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
        if (isEqualSignComparator()) {
            return compareResult == 0;
        } else if (isUnequalSignComparator()) {
            return compareResult != 0;
        } else if (this.isLiteralValue && isLikeCompare()) {
            return s.contains(this.value);
        } else if (isLikeCompare()) {
            return s.toLowerCase().contains(this.value.toLowerCase());
        }
        return false;
    }

    private boolean isBoolValueCompare(String boolValue) {
        if (isEqualSignComparator()) {
            return stringsEqualCaseInsensitively(this.value, boolValue);
        } else if (isUnequalSignComparator()) {
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
            if (isNotDigitalChar(c) && isFirstNotCharSign(i, c)) {
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
            if (isNotDigitalChar(c) && isFirstNotCharSign(i, c) && i != index) {
                return false;
            }
        }
        return true;
    }

    private boolean isNotDigitalChar(char c) {
        return '0' > c || c > '9';
    }

    private boolean isFirstNotCharSign(int index, char c) {
        return index == 0 && c != '-' && c != '+';
    }

    private boolean isEqualSignComparator() {
        return this.operator.compareTo("==") == 0;
    }

    private boolean isLikeCompare() {
        return this.operator.toUpperCase().compareTo("LIKE") == 0;
    }

    private boolean isUnequalSignComparator() {
        return this.operator.compareTo("!=") == 0;
    }

    private boolean isBiggerThanComparator() {
        return this.operator.compareTo(">") == 0;
    }

    private boolean isBiggerThanOrEqualComparator() {
        return this.operator.compareTo(">=") == 0;
    }

    private boolean isSmallerThanComparator() {
        return this.operator.compareTo("<") == 0;
    }

    private boolean isSmallerThanOrEqualComparator() {
        return this.operator.compareTo("<=") == 0;
    }

}
