package edu.uob;

<<<<<<< HEAD
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
=======
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class DBTable {
    private final String tableName;
    private int colNum;
    private List<String> colNames;
    private List<Integer> idUsed;
    private List<List<String>> rows;


    public DBTable(String name) {
        this.tableName = name;
        this.colNum = 0;
        this.idUsed = new LinkedList<>();
        this.colNames = new LinkedList<>();
        this.rows = new LinkedList<>();
    }

    public String addRow(List<String> line) {
        if (line.size() != this.colNum - 1) {
            return "Not expected number of values. Cannot insert into table " + this.tableName;
        }
        List<String> row = new LinkedList<>();
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
        return null;
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

    public boolean deleteRow(DBCmd dbCmd) {
        return this.rows.removeIf(row -> dbCmd.evaluateConditions(row, this.colNames)) && !dbCmd.hasInterpretError();
    }

    public String setColNames(List<String> line, boolean isJoin) {
        this.colNames.add("id");
        for (String l : line) {
            if (containsAttribute(l)) {
                return "Failed to add " + l + ". Cannot add duplicate ColNames";
            }
            if (l.startsWith("'") && l.endsWith("'")) {
                this.colNames.add(l.substring(1, l.length() - 1));
            } else if (l.contains(".") && !isJoin) {
                int index = l.indexOf('.');
                this.colNames.add(l.substring(index + 1));
            } else {
                this.colNames.add(l);
            }
        }
        this.colNum = colNames.size();
        return null;
    }

    public void setColNamesNoNeedToAddId(List<String> line) {
        this.colNames.addAll(line);
        this.colNum = colNames.size();
    }

    public List<String> getColNames() {
        return this.colNames;
>>>>>>> develop
    }

    public String getTableName() {
        return this.tableName;
    }

<<<<<<< HEAD
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
=======
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
        int colIndex = getIndexOfAttribute(colName);
        this.colNames.remove(colIndex);
        this.rows.forEach(row -> row.remove(colIndex));
        this.colNum = colNames.size();
    }

    public String addCol(String colName) {
        String name = colName;
        if (containsAttribute(name)) {
            return name + " already exists in " + this.tableName;
        } else if (compareStringsCaseInsensitively(name, "id")) {
            return "id cannot be added by user manually";
        }
        if (this.colNum == 0) {
            colNames.add("id");
        }
        if (name.contains(".")) {
            int index = name.indexOf('.');
            String tbName = name.substring(0, index);
            if (!compareStringsCaseInsensitively(tbName, this.tableName)) {
                return tbName + " does not match table name " + this.tableName;
            }
            name = name.substring(index + 1);
        }
        colNames.add(name);
        this.rows.forEach(row -> row.add("NULL"));
        this.colNum += 1;
        return null;
    }

    public boolean containsAttribute(String colName) {
        String name = colName;
        if (name.contains(".")) {
            int index = name.indexOf('.');
            String tbName = name.substring(0, index);
            String attributeName = name.substring(index + 1);
            if (!compareStringsCaseInsensitively(tbName, this.tableName)) {
                return false;
            }
            name = attributeName;
        }
        for (String n : this.colNames) {
            if (compareStringsCaseInsensitively(n, name)) {
                return true;
            }
        }
        return false;
    }

    private boolean compareStringsCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().compareTo(s2.toLowerCase()) == 0;
    }

    public boolean update(Map<String, String> nameValueList, DBCmd dbCmd) {
        String error = namesExist(nameValueList);
        if (error != null) {
            dbCmd.setErrorMessage(error);
            return false;
        }
        for (List<String> row : this.rows) {
            if (dbCmd.evaluateConditions(row, this.colNames)) {
                for (String key : nameValueList.keySet()) {
                    int index = getIndexOfAttribute(key);
                    row.set(index, nameValueList.get(key));
                    if (compareStringsCaseInsensitively(key, "id")) {
                        dbCmd.setErrorMessage("id cannot be changed");
                        return false;
                    }
                }
            }
            if (dbCmd.hasInterpretError()) {
                return false;
            }
        }
        return true;
    }

    private String namesExist(Map<String, String> nameValueList) {
        for (String key : nameValueList.keySet()) {
            if (!containsAttribute(key)) {
                return "Table " + this.tableName + " does not have a column name " + key;
            }
        }
        return null;
    }

    public boolean failToFile(String fileToWrite) {
        try {
            FileWriter writer = new FileWriter(fileToWrite);
            writer.write(toString());
            writer.flush();
            writer.close();
            return false;
        } catch (IOException ioException) {
            return true;
        }
    }

    private String listToString(List<String> stringList) {
        return stringList.stream().reduce("", (s1, s2) -> String.join("\t", s1, s2)).trim();
    }

    public String toString() {
        return listToString(this.getColNames()) + "\n" + rowsToString();
    }

    private String rowsToString() {
        return this.rows.stream().map(this::listToString).reduce("", (s1, s2) -> String.join("\n", s1, s2)).trim();
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
            matches.add(getIndexOfAttribute(attribute));
        }
        return matches;
    }

    public int getIndexOfAttribute(String attribute) {
        String attr = attribute;
        if (attr.contains(".")) {
            int index = attr.indexOf('.');
            attr = attr.substring(index + 1);
        }
        for (int i = 0; i < this.colNames.size(); i++) {
            if (compareStringsCaseInsensitively(this.colNames.get(i), attr)) {
                return i;
            }
        }
        return -1;
    }

    public String selectedContentToString(List<String> attributeList) {
        List<Integer> indexOfQueriedAttributes = getIndexOfQueriedAttributes(attributeList);
        String s1 = selectedColNamesToString(indexOfQueriedAttributes);
        String s2 = selectedRowsAttributesToString(indexOfQueriedAttributes);
        return s1 + s2;
    }
    public String selectedContentToString(List<String> attributeList, DBCmd dbCmd) {
        List<Integer> indexOfQueriedAttributes = getIndexOfQueriedAttributes(attributeList);
        String s1 = selectedColNamesToString(indexOfQueriedAttributes);
        String s2 = selectedRowsAttributesToString(indexOfQueriedAttributes, dbCmd);
        return s1 + s2;
    }
    public String selectedContentToString(DBCmd dbCmd) {
        return listToString(this.colNames) + "\n" + selectedRowsToString(dbCmd);
    }

    private String selectedRowsToString(DBCmd dbCmd) {
        StringBuilder accumulator = new StringBuilder();
        for (List<String> row : this.rows) {
            if (dbCmd.evaluateConditions(row, this.colNames)) {
                if (dbCmd.hasInterpretError()) {
                    return null;
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
            accumulator.append(selectedStringFromList(row, indexOfQueriedAttributes));
            accumulator.append("\n");
        }
        return accumulator.toString().trim();
    }

    private String selectedRowsAttributesToString(List<Integer> indexOfQueriedAttributes, DBCmd dbCmd) {
        StringBuilder accumulator = new StringBuilder();
        for (List<String> row : this.rows) {
            if (dbCmd.evaluateConditions(row, this.colNames)) {
                if (dbCmd.hasInterpretError()) {
                    return null;
                }
                accumulator.append(selectedStringFromList(row, indexOfQueriedAttributes)).append("\n");
            }
        }
        return accumulator.toString().trim();
    }

    private String selectedStringFromList(List<String> stringList, List<Integer> indexOfQueriedAttributes) {
        StringBuilder accumulator = new StringBuilder();
        for (Integer indexOfQueriedAttribute : indexOfQueriedAttributes) {
            accumulator.append(stringList.get(indexOfQueriedAttribute));
            accumulator.append("\t");
        }
        return accumulator.toString().trim();
    }



>>>>>>> develop
}
