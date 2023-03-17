package edu.uob;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    public DBTable(String name, List<String> firstLine) {
        this.tableName = name;
        this.rowNum = 0;
        this.colNum = firstLine.size() + 1;
        this.idUsed = new ArrayList<>();
        this.colNames = new ArrayList<>();
        colNames.add("id");
        colNames.addAll(firstLine);
        this.rows = new ArrayList<>();
    }

    public String addRow(List<String> line) {
        if (line.size() != this.colNum - 1) {
            return "[ERROR] Not expected number of values. Cannot insert into table " + this.tableName;
        }
        List<String> row = new ArrayList<>();
        if (idUsed.isEmpty()) {
            row.add("1");
            idUsed.add(1);
        } else {
            int newId = idUsed.get(idUsed.size() - 1) + 1;
            row.add(Integer.toString(newId));
        }
        this.rows.add(row);
        this.rowNum += 1;
        return "[OK] Successfully inserted values into table " + this.tableName;
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

    public List<String> getColNames() {
        return this.colNames;
    }

    public String getTableName() {
        return this.tableName;
    }

    public void changeValue(int primaryKey, String column, String newValue) {
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
    }

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

    public String dropCol(String colName) {
        if (compareStringsCaseInsensitively(colName, "id")) {
            return "[ERROR] Cannot delete column id of table " + this.tableName;
        }
        if (!listContainsString(this.colNames, colName)) {
            return "[ERROR] " + tableName + " does not have attribute " + colName;
        }
        int colIndex = colNames.indexOf(colName) + 1;
        colNames.remove(colName);
        this.rows.forEach(row -> row.remove(colIndex));
        this.colNum -= 1;
        return "[OK] Successfully removed " + colName + " from " + tableName;
    }

    public String addCol(String colName) {
        if (listContainsString(this.colNames, colName)) {
            return "[ERROR] " + tableName + " already have attribute " + colName;
        }
        colNames.add(colName);
        this.rows.forEach(row -> row.add(""));
        this.colNum += 1;
        return "[OK] Successfully added " + colName + " to " + tableName;
    }

    private boolean listContainsString(List<String> stringList, String s) {
        return stringList.stream().map(String::toLowerCase).toList().contains(s.toLowerCase());
    }

    private boolean compareStringsCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public String toFile(String fileToWrite) {
        try {
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(listToString(this.getColNames()) + "\n");
            writer.write(this.rows.stream()
                    .map(this::listToString)
                    .reduce("", (s1, s2) -> String.join("\n", s1, s2))
                    .trim());
            writer.flush();
            writer.close();
            return "[OK] ";
        } catch (IOException ioException) {
            return "[ERROR] Failed to write to file " + fileToWrite;
        }
    }

    private String listToString(List<String> stringList) {
        return stringList.stream()
                .reduce("", (s1, s2) -> String.join("\t", s1, s2))
                .trim();
    }
}
