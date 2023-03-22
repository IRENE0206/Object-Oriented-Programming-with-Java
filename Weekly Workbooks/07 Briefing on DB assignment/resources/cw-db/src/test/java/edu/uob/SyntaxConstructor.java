package edu.uob;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SyntaxConstructor {
    Random random;

    public SyntaxConstructor() {
        random = new Random();
    }

    String arbitraryWhiteSpaceGenerator() {
        int r = random.nextInt(10);
        return " ".repeat(r);
    }

    String arbitraryCaseGenerator(String keyword) {
        StringBuilder accumulator = new StringBuilder();
        for (int i = 0; i < keyword.length(); i++) {
            int r = random.nextInt(10);
            char c = keyword.charAt(i);
            if (r / 2 == 0) {
                accumulator.append(Character.toUpperCase(c));
            } else {
                accumulator.append(Character.toLowerCase(c));
            }
        }
        return accumulator.toString();
    }

    public String insertArbitraryWhiteSpaces(List<String> strings) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String s : strings) {
            stringBuilder.append(arbitraryWhiteSpaceGenerator());
            stringBuilder.append(s);
        }
        stringBuilder.append(arbitraryWhiteSpaceGenerator());
        return stringBuilder.toString();
    }

    public String useCommand(String databaseName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("USE "));
        strings.add(arbitraryCaseGenerator(databaseName));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String createDataBaseCommand(String databaseName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("CREATE DATABASE "));
        strings.add(databaseName);
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String createTableCommand(String tableName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("CREATE TABLE "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String createTableCommand(String tableName, String attributeList) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("CREATE TABLE "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add("(");
        strings.add(attributeList);
        strings.add(")");
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String dropDatabaseCommand(String databaseName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("DROP DATABASE "));
        strings.add(arbitraryCaseGenerator(databaseName));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String dropTableCommand(String tableName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("DROP TABLE "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String alterCommand(String tableName, String alterationType, String attributeName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("ALTER TABLE "));
        strings.add(tableName);
        strings.add(" ");
        strings.add(arbitraryCaseGenerator(alterationType));
        strings.add(" ");
        strings.add(attributeName);
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String insertCommand(String tableName, String valueList) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("INSERT INTO "));
        strings.add(tableName);
        strings.add(arbitraryCaseGenerator(" VALUES("));
        strings.add(valueList);
        strings.add(")");
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String selectCommand(String wildAttribLit, String tableName) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("SELECT "));
        strings.add(arbitraryCaseGenerator(wildAttribLit));
        strings.add(arbitraryCaseGenerator(" FROM "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String selectCommand(String wildAttribLit, String tableName, String condition) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("SELECT "));
        strings.add(arbitraryCaseGenerator(wildAttribLit));
        strings.add(arbitraryCaseGenerator(" FROM "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(arbitraryCaseGenerator(" WHERE "));
        strings.add(condition);
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String updateCommand(String tableName, String nameValueList, String condition) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("UPDATE "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(arbitraryCaseGenerator(" SET "));
        strings.add(nameValueList);
        strings.add(arbitraryCaseGenerator(" WHERE "));
        strings.add(condition);
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String deleteCommand(String tableName, String condition) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("DELETE FROM "));
        strings.add(arbitraryCaseGenerator(tableName));
        strings.add(arbitraryCaseGenerator(" WHERE "));
        strings.add(condition);
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String joinCommand(String tableName1, String tableName2, String attributeName1, String attributeName2) {
        List<String> strings = new ArrayList<>();
        strings.add(arbitraryCaseGenerator("JOIN "));
        strings.add(arbitraryCaseGenerator(tableName1));
        strings.add(arbitraryCaseGenerator(" AND "));
        strings.add(arbitraryCaseGenerator(tableName2));
        strings.add(arbitraryCaseGenerator(" ON "));
        strings.add(arbitraryCaseGenerator(attributeName1));
        strings.add(arbitraryCaseGenerator(" AND "));
        strings.add(arbitraryCaseGenerator(attributeName2));
        strings.add(";");
        return insertArbitraryWhiteSpaces(strings);
    }

    public String randomNameGenerator() {
        StringBuilder randomName = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            randomName.append((char) (97 + (Math.random() * 25.0)));
        }
        return randomName.toString();
    }

    public boolean stringContainsCaseInsensitively(String s1, String s2) {
        return s1.toLowerCase().contains(s2.toLowerCase());
    }
}
