package edu.uob;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class DBTable {
    private String tableName;
    private int rowNum;
    private int colNum;
    private List<String> colNames;
    private List<Integer> idUsed;
    private List<List<String>> rows;


    public DBTable(String name) {
        this.tableName = name;
        this.rowNum = 0;
        this.colNum = 0;
        this.idUsed = new ArrayList<>();
        this.colNames = new ArrayList<>();
        this.rows = new ArrayList<>();
    }

    public String addRow(List<String> line) {
        if (line.size() != this.colNum - 1) {
            System.out.println("line " + line.size());
            this.colNames.forEach(System.out::println);
            System.out.println(this.colNum);
            return "Not expected number of values. Cannot insert into table " + this.tableName;
        }
        List<String> row = new ArrayList<>();
        if (idUsed.isEmpty()) {
            row.add("1");
            addUsedID(1);
        } else {
            int newId = getNewID();
            row.add(Integer.toString(newId));
            addUsedID(newId);
        }
        row.addAll(line);
        this.rows.add(row);
        this.rowNum += 1;
        // this.rows.forEach(System.out::println);
        return "";
    }

    public boolean isEmpty() {
        return this.colNum == 0;
    }

    private void addUsedID(Integer id) {
        this.idUsed.add(id);
    }

    private int getNewID() {
        return idUsed.get(idUsed.size() - 1) + 1;
    }

    public void setRows(List<String> row) {
        this.rows.add(row);
        addUsedID(Integer.valueOf(row.get(0)));
    }

    public String deleteRow(String id) {
        for (List<String> row : this.rows) {
            if (compareStringsCaseInsensitively(row.get(0), id)) {
                rows.remove(row);
                this.rowNum -= 1;
                return "[OK] Successfully delete values from table " + this.tableName;
            }
        }
        return "[ERROR] Cannot find entry with primaryId " + id;
    }

    public void setColNames(List<String> line) {
        this.colNames.add("id");
        for (String l : line) {
            if (l.startsWith("'") && l.endsWith("'")) {
                this.colNames.add(l.substring(1, l.length() - 1));
            } else {
                this.colNames.add(l);
            }
        }
        // this.colNames.forEach(System.out::println);
        this.colNum = colNames.size();
    }

    public void setColNamesNoNeedToAddId(List<String> line) {
        this.colNames.addAll(line);
        this.colNames.forEach(System.out::println);
        this.colNum = colNames.size();
    }

    public List<String> getColNames() {
        return this.colNames;
    }

    public String getTableName() {
        return this.tableName;
    }

    /* public void changeValue(int primaryKey, String column, String newValue) {
        try {
            int i = getColIndex(column);
            try {
                List<String> targetRow = getRow(primaryKey);
                targetRow.set(i, newValue);
            } catch (NoSuchElementException noSuchElementException) {
                System.out.println("There is no entry with id " + primaryKey);
            }
        } catch (NoSuchElementException noSuchElementException) {
            System.out.println(column + " doesn't exit");
        }
    } */

    private int getColIndex(String column) {
        List<String> lowerCaseColNames = colNames.stream().map(String::toLowerCase).toList();
        int index = lowerCaseColNames.indexOf(column.toLowerCase());
        if (index == -1) {
            throw new NoSuchElementException();
        }
        return index;
    }

    public List<String> getRow(String id) {
        for (List<String> r: this.rows) {
            if (compareStringsCaseInsensitively(r.get(0), id)) {
                return r;
            }
        }
        return null;
    }

    public List<List<String>> getRows() {
        return this.rows;
    }

    public void dropCol(String colName) {
        if (compareStringsCaseInsensitively(colName, "id")) {
            return;
        }
        if (!containsAttribute(colName)) {
            return;
        }
        int colIndex = colNames.indexOf(colName) + 1;
        colNames.remove(colName);
        this.rows.forEach(row -> row.remove(colIndex));
        this.colNum -= 1;
    }

    public void addCol(String colName) {
        if (containsAttribute(colName)) {
            return;
        }
        colNames.add(colName);
        this.rows.forEach(row -> row.add(""));
        this.colNum += 1;
    }

    public boolean containsAttribute(String colName) {
        return listContainsString(this.colNames, colName);
    }

    private boolean listContainsString(List<String> stringList, String s) {
        return stringList.stream().map(String::toLowerCase).toList().contains(s.toLowerCase());
    }

    private boolean compareStringsCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public boolean failToFile(String fileToWrite) {
        // System.out.println(fileToWrite);
        try {
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(toString());
            writer.flush();
            writer.close();
            return false;
        } catch (IOException ioException) {
            // System.out.println("WHY");
            return true;
        }
    }

    private String listToString(List<String> stringList) {
        return stringList.stream()
                .reduce("", (s1, s2) -> String.join("\t", s1, s2))
                .trim();
    }

    public String toString() {
        return listToString(this.getColNames()) + "\n" + rowsToString();
    }

    private String rowsToString() {
        return this.rows.stream()
                .map(this::listToString)
                .reduce("", (s1, s2) -> String.join("\n", s1, s2))
                .trim();
    }

    public boolean containsQueriedAttributes(List<String> attributeList) {
        for (String attribute : attributeList) {
            if (!containsAttribute(attribute)) {
                return false;
            }
        }
        return true;
    }

    private List<Integer> getIndexOfQueriedAttributes(List<String> attributeList) {
        List<Integer> matches = new ArrayList<>();
        for (String attribute : attributeList) {
            matches.add(this.colNames.indexOf(attribute));
        }
        return matches;
    }

    public String selectedContentToString(List<String> attributeList) {
        List<Integer> indexOfQueriedAttributes = getIndexOfQueriedAttributes(attributeList);
        return selectedColNamesToString(indexOfQueriedAttributes) + selectedRowsAttributesToString(indexOfQueriedAttributes);
    }
    public String selectedContentToString(List<String> attributeList, DBCmd dbCmd) {
        List<Integer> indexOfQueriedAttributes = getIndexOfQueriedAttributes(attributeList);
        return selectedColNamesToString(indexOfQueriedAttributes) + selectedRowsAttributesToString(indexOfQueriedAttributes, dbCmd);
    }
    public String selectedContentToString(DBCmd dbCmd) {
        return listToString(this.colNames) + "\n" + selectedRowsToString(dbCmd);
    }

    private String selectedRowsToString(DBCmd dbCmd) {
        StringBuilder accumulator = new StringBuilder();
        for (List<String> row : this.rows) {
            if (dbCmd.evaluateConditions(row, this.colNames)) {
                if (dbCmd.isInterpretError()) {
                    return "";
                }
                for (String s : row) {
                    accumulator.append(s).append("\t");
                }
                accumulator.append("\n");
            }
        }
        return accumulator.toString().trim();
    }

    private String selectedColNamesToString(List<Integer> indexOfQueriedAttributes) {
        return selectedStringFromList(this.colNames, indexOfQueriedAttributes).concat("\n");
    }

    private String selectedRowsAttributesToString(List<Integer> indexOfQueriedAttributes) {
        StringBuilder accumulator = new StringBuilder();
        for (List<String> row : this.rows) {
            accumulator.append(selectedStringFromList(row, indexOfQueriedAttributes)).append("\n");
        }
        return accumulator.toString().trim();
    }

    private String selectedRowsAttributesToString(List<Integer> indexOfQueriedAttributes, DBCmd dbCmd) {
        StringBuilder accumulator = new StringBuilder();
        for (List<String> row : this.rows) {
            if (dbCmd.evaluateConditions(row, this.colNames)) {
                if (dbCmd.isInterpretError()) {
                    return "";
                }
                accumulator.append(selectedStringFromList(row, indexOfQueriedAttributes)).append("\n");
            }
        }
        return accumulator.toString().trim();
    }

    private String selectedStringFromList(List<String> stringList, List<Integer> indexOfQueriedAttributes) {
        StringBuilder accumulator = new StringBuilder();
        for (Integer index : indexOfQueriedAttributes) {
            accumulator.append(stringList.get(index)).append("\t");
        }
        return accumulator.toString().trim();
    }



}
