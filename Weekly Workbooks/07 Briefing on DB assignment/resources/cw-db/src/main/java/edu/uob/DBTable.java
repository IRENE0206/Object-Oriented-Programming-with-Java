package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

public class DBTable {
    private String tableName;
    private int rowNum;
    private int colNum;
    private List<Integer> ids;
    private List<String> colNames;
    private List<List<String>> values;

    public DBTable(String name, String[] firstLine) {
        this.tableName = name.substring(0, name.indexOf(".tab"));
        this.rowNum = 0;
        this.colNum = firstLine.length;
        this.ids = new ArrayList<>();
        this.colNames = new ArrayList<>(colNum);
        colNames.addAll(Arrays.asList(firstLine));
        this.values = new ArrayList<>();
    }

    public void addRow(String[] row) {
        this.ids.add(Integer.valueOf(row[0]));
        List<String> line = new ArrayList<>(this.colNum);
        line.addAll(Arrays.asList(row));
        this.values.add(line);
        this.rowNum += 1;
    }

    public String getColNames() {
        return String.join("\t", this.colNames);
    }

    public String getValues() {
        List<String> lines = new ArrayList<>();
        this.values.forEach(l -> lines.add(String.join("\t", l)));
        return String.join("\n", lines);
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

    private int getColIndex(String column) throws NoSuchElementException {
        List<String> lowerCaseColNames = colNames.stream().map(String::toLowerCase).toList();
        int index = lowerCaseColNames.indexOf(column.toLowerCase());
        if (index == -1) {
            throw new NoSuchElementException();
        }
        return index;
    }

    private List<String> getRow(int primaryKey) throws NoSuchElementException {
        List<String> result = null;
        for (List<String> r: this.values) {
            if (r.get(0).compareTo(String.valueOf(primaryKey)) == 0) {
                result = r;
            }
        }
        if (result == null) {
            throw new NoSuchElementException();
        }
        return result;
    }
}
