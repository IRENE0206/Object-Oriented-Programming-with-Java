package edu.uob;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parser {
    private DBServer dbServer;
    private Tokeniser tokeniser;
    private Boolean parsedOK;
    private String errorMessage;

    public Parser(DBServer dbServer, String query) {
        this.dbServer = dbServer;
        this.tokeniser = new Tokeniser(query);
        this.parsedOK = false;
        this.errorMessage = "[ERROR] ";
    }

    public DBCmd parse() {
        return getCommandType();
    }

    private DBCmd getCommandType() {
        String firstKeyword = tokeniser.getToken();
        if (!tokeniser.hasNextToken()) {
            return null;
        }
        tokeniser.nextToken();
        if (toUse(firstKeyword)) {
            return use();
        } else if (toCreate(firstKeyword)) {
            return create();
        } else if (toDrop(firstKeyword)) {
            return drop(tokensLeft);
        } else if (toAlter(firstKeyword)) {
            return alter(tokensLeft);
        } else if (toInsert(firstKeyword)) {
            if (tokens.size() < 2) {
                return "[ERROR] " + "Invalid query";
            }
            String secondKeyword = tokens.get(1);
            if (stringsEqualCaseInsensitively(secondKeyword, "INTO")) {
                
            } else {
                return "[ERROR] " + "Invalid query";
            }
        } else if (toSelect(firstKeyword)) {
            
        } else if (toUpdate(firstKeyword)) {
            
        } else if (toDelete(firstKeyword)) {
            if (tokens.size() < 2) {
                return "[ERROR] " + "Invalid query";
            }
            String secondKeyword = tokens.get(1);
            if (stringsEqualCaseInsensitively(secondKeyword, "FROM")) {

            } else {
                return "[ERROR] " + "Invalid query";
            }
        } else if (toJoin(firstKeyword)) {
            
        } else {
            return "[ERROR] Invalid keyword";
        }
    }

    private boolean toUse(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "USE");
    }

    private UseCMD use() {
        String databaseName = tokeniser.getToken();
        if (!isValidDatabaseName(databaseName)) {
            setDatabaseNameErrorMessage(databaseName);
            return null;
        }
        if (!tokeniser.hasNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        tokeniser.nextToken();
        if (!isSemiColon(tokeniser.getToken()) || tokeniser.hasNextToken()) {
            setErrorMessage("Invalid syntax");
            return null;
        }
        setParsedOK();
        return new UseCMD(databaseName);
    }

    private void setParsedOK() {
        this.parsedOK = true;
    }

    private void setDatabaseNameErrorMessage(String databaseName) {
        setErrorMessage(databaseName + " is not valid [DatabaseName]");
    }

    private void setMissingSemiColonErrorMessage() {
        setErrorMessage("Missing ; at the end");
    }

    private void setErrorMessage(String message) {
        this.errorMessage += message;
    }

    private boolean isValidDatabaseName(String s) {
        return isPlainText(s);
    }

    private boolean isSemiColon(String s) {
        return s.compareTo(";") == 0;
    }

    private CreateCMD create() {
        String databaseOrTable = tokeniser.getToken();
        if (!tokeniser.hasNextToken()) {
            setErrorMessage("Invalid syntax");
            return null;
        }
        tokeniser.nextToken();
        if (toCreateDatabase(databaseOrTable)) {
            return createDatabaseCMD();
        } else if (toCreateTable(databaseOrTable)) {
            return createTableCMD();
        } else {
            setErrorMessage("Invalid syntax");
            return null;
        }
    }

    private boolean toCreateDatabase(String databaseOrTable) {
        return stringsEqualCaseInsensitively(databaseOrTable, "DATABASE");
    }

    private boolean toCreateTable(String databaseOrTable) {
        return stringsEqualCaseInsensitively(databaseOrTable, "TABLE");
    }

    private CreateDatabaseCMD createDatabaseCMD() {
        String databaseName = tokeniser.getToken();
        if (!isValidDatabaseName(databaseName)) {
            setDatabaseNameErrorMessage(databaseName);
            return null;
        }
        if (!tokeniser.hasNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        tokeniser.nextToken();
        if (!isSemiColon(tokeniser.getToken()) || tokeniser.hasNextToken()) {
            setErrorMessage("Invalid syntax");
            return null;
        }
        setParsedOK();
        return new CreateDatabaseCMD(databaseName);
    }

    private CreateTableCMD createTableCMD() {
        String tableName = tokeniser.getToken();
        if (!isValidTableName(tableName)) {
            setTableNameErrorMessage(tableName);
            return null;
        }
        if (!tokeniser.hasNextToken()) {
            setSyntaxErrorMessage();
        }
        tokeniser.nextToken();
        String nextToken = tokeniser.getToken();
        if (isSemiColon(nextToken) && !tokeniser.hasNextToken()) {
            setParsedOK();
            return new CreateTableCMD(tableName);
        } else if (isOpenBracket(nextToken) && tokeniser.hasNextToken()) {
            tokeniser.nextToken();
            List<String> tableList = new ArrayList<>();
            List<String> attributeList = new ArrayList<>();
            getAttributeList(tableList, attributeList);
            if (attributeList.size() == 0) {
                return null;
            }
            if (!isCloseBracket(tokeniser.getToken())) {
                setMissingBracketMessage(")");
                return null;
            }
            if (!tokeniser.hasNextToken()) {
                setSyntaxErrorMessage();
            }
            tokeniser.nextToken();
            if (!isSemiColon(tokeniser.getToken()) || tokeniser.hasNextToken()) {
                setErrorMessage("Invalid syntax");
                return null;
            }
            setParsedOK();
            return new CreateTableCMD(tableName, tableList, attributeList);
        }
    }

    private void setSyntaxErrorMessage() {
        setErrorMessage("Invalid syntax");
    }

    private void setMissingBracketMessage(String bracket) {
        setErrorMessage("Missing " + bracket);
    }

    private boolean isValidAttributeName(String s, List<String> tableNames, List<String> attributeList) {
        if (isPlainText(s)) {
            attributeList.add(s);
            return true;
        } else if (!s.contains(".")) {
            return false;
        }
        int strLength = s.length();
        int index = s.indexOf(".");
        if (index == strLength - 1) {
            return false;
        }
        String tableName = s.substring(0, index);
        String plainText = s.substring(index + 1);
        if (isValidTableName(tableName) && isPlainText(plainText)) {
            tableNames.add(tableName);
            attributeList.add(plainText);
            return true;
        }
        return false;
    }

    private void getAttributeList(List<String> tableNames, List<String> accumulator) {
        String token = tokeniser.getToken();
        if (isValidAttributeName(token, tableNames, accumulator)) {
            if (!tokeniser.hasNextToken()) {
                setSyntaxErrorMessage();
                return;
            }
            tokeniser.nextToken();
            String nextToken = tokeniser.getToken();
            if (!isComma(nextToken)) {
                accumulator.add(token);
            } else if (!tokeniser.hasNextToken()) {
                setSyntaxErrorMessage();
            } else {
                accumulator.add(token);
                tokeniser.nextToken();
                getAttributeList(tableNames, accumulator);
            }
        } else {
            setSyntaxErrorMessage();
        }
    }

    private boolean isValidTableName(String tableName) {
        return isPlainText(tableName);
    }

    private void setTableNameErrorMessage(String tableName) {
        setErrorMessage(tableName + " is not valid for [TableName]");
    }

    private String useDatabase(String databaseName) {
        if (!isPlainText(databaseName)) {
            return errorMessage(databaseName, "[PlainText]");
        }

        return ok + "Changed the database to " + databaseName;
    }

    private String createDB(String databaseName) {
        if (!isPlainText(databaseName)) {
            return errorMessage(databaseName, "[PlainText]");
        }
        String name = getDBPath(databaseName);
        File databaseToCreate = new File(name);
        try {
            boolean createSuccessful = databaseToCreate.createNewFile();
            if (!createSuccessful) {
                return error + databaseName + "already exists";
            } else if (!databaseToCreate.isDirectory()) {
                if (!databaseToCreate.delete()) {
                    return error + databaseName + "is not a directory and can't be deleted";
                }
                return error + databaseName + "is not a directory";
            }
            return ok + "Successfully creates database " + databaseName;
        } catch (IOException ioException) {
            return error + "Failed to create database " + databaseName;
        }
    }

    private String createTableWithoutAttributes(String tableName) {
        if (!isPlainText(tableName)) {
            return errorMessage(tableName, "[PlainText]");
        }
        String name = getTablePath(tableName);
        File tableToCreate = new File(name);
        try {
            boolean createSuccessful = tableToCreate.createNewFile();
            if (!createSuccessful) {
                return error + tableToCreate + "already exists";
            } else if (!tableToCreate.isFile()) {
                if (!tableToCreate.delete()) {
                    return error + tableToCreate + " is not a file and can't be deleted";
                }
                return error + tableToCreate + " is not a file";
            }
            return ok + "Successfully created table " + tableToCreate;
        } catch (IOException ioException) {
            return error + "Failed to create table " + tableToCreate;
        }
    }

    private String createTableWithAttributes(String tableName, List<String> tokens) {
        int tokenCount = tokens.size();
        if (tokens.size() <= 2) {
            return syntaxErrorMessage();
        }
        if (!isOpenBracket(tokens.get(0))) {
            return error + "Missing (";
        }
        if (!isCloseBracket(tokens.get(tokenCount - 1))) {
            return error + "Missing )";
        }
        List<String> tokensLeft = tokens.subList(1, tokenCount - 1);
        List<String> firstLine = new ArrayList<>();
        for (int i = 0; i < tokenCount - 2; i++) {
            String s = tokensLeft.get(i);
            if (i / 2 == 0 && !isComma(s)) {
                s = stripAttributeName(s, tableName);
                if (isPlainText(s)) {
                    firstLine.add(s);
                } else {
                    return errorMessage(s, "AttributeName");
                }
            } else if ((i / 2 != 0 && !isComma(s)) || (i / 2 == 0 && isComma(s))) {
                return syntaxErrorMessage();
            }
        }
        String message = createTableWithoutAttributes(tableName);
        if (message.startsWith(ok)) {
            String name = getTablePath(tableName);
            File fileToOpen = new File(name);
            DBTable dbTable = new DBTable(tableName, firstLine);
            this.dbServer.tableToFile(dbTable, fileToOpen);
            return ok + "Successfully create table " + tableName;
        }
        return message;
    }

    private String stripAttributeName(String s, String tableName) {
        if (s.startsWith(tableName + ".")) {
            return s.substring(s.indexOf(".") + 1);
        }
        return s;
    }

    private String alter(List<String> tokens) {
        if (tokens.size() != 4) {
            return syntaxErrorMessage();
        }
        String secondKeyword = tokens.get(0);
        if (!toActOnTable(secondKeyword)) {
            return errorMessage(secondKeyword, "TABLE");
        }
        String tableName = tokens.get(1);
        String fileName = getTablePath(tableName);
        File fileToAlter = new File(fileName);
        if (!isExistingTable(fileToAlter)) {
            return error + tableName + " doesn't exist";
        }
        String alterType = tokens.get(2);
        if (isAdd(alterType) || isDrop(alterType)) {
            DBTable dbTable = this.dbServer.fileToTable(fileName);
            String attributeName = tokens.get(3);
            String message;
            boolean hasAttribute = dbTable.getColNames().contains(attributeName);
            if (isDrop(alterType)) {
                message = dbTable.dropCol(attributeName);
            } else {
                message = dbTable.addCol(attributeName);
            }
            this.dbServer.tableToFile(dbTable, fileToAlter);
            return message;
        } else {
            return errorMessage(alterType, "[AlterationType]");
        }
    }

    private boolean isExistingAttribute(File tableFile, String attributeName) {

    }

    private boolean isComma(String s) {
        return s.compareTo(",") == 0;
    }

    private boolean isOpenBracket(String s) {
        return s.compareTo("(") == 0;
    }

    private boolean isCloseBracket(String s) {
        return s.compareTo(")") == 0;
    }

    private String errorMessage(String s, String expected) {
        return error + s + " is not valid" + expected;
    }

    private boolean toCreate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "CREATE");
    }

    private boolean toActOnDB(String secondKeyword) {
        return stringsEqualCaseInsensitively(secondKeyword, "DATABASE");
    }

    private boolean toActOnTable(String secondKeyword) {
        return stringsEqualCaseInsensitively(secondKeyword, "TABLE");
    }

    private boolean toDrop(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "DROP");
    }

    private String drop(List<String> tokens) {
        if (tokens.size() != 2) {
            return syntaxErrorMessage();
        }
        String secondKeyword = tokens.get(0);
        String name = tokens.get(1);
        if (toActOnDB(secondKeyword)) {
            return dropDB(name);
        } else if (toActOnTable(secondKeyword)) {
            return dropTable(name);
        } else {
            return syntaxErrorMessage();
        }
    }

    private String dropDB(String databaseName) {
        String name = getDBPath(databaseName);
        File fileToDrop = new File(name);
        if (!isExistingDB(fileToDrop)) {
            return error + databaseName + " is not existing database";
        }
        File[] fileInDatabase = fileToDrop.listFiles();
        if (fileInDatabase != null) {
            for (File f : fileInDatabase) {
                if (!f.delete()) {
                    return error + "Failed to delete " + f + " in database " + databaseName;
                }
            }
        }
        if (fileToDrop.delete()) {
            return ok + "Successfully deleted database " + databaseName;
        } else {
            return error + "Failed to delete database " + databaseName;
        }
    }

    private String dropTable(String tableName) {
        String name = getTablePath(tableName);
        File fileToDrop = new File(name);
        if (!isExistingTable(fileToDrop)) {
            return error + tableName + " is not existing table in current database";
        }
        if (fileToDrop.delete()) {
            return ok + "Successfully deleted database " + tableName;
        } else {
            return error + "Failed to delete database " + tableName;
        }
    }

    private String getTablePath(String tableName) {
        return this.dbServer.getCurrentDataBasePath() + File.separator + tableName + tableFileExtension;
    }

    private boolean isExistingTable(File table) {
        return table.exists() && table.getName().endsWith(tableFileExtension);
    }

    private boolean toAlter(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "ALTER");
    }

    private boolean toInsert(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "INSERT");
    }

    private String insert(List<String> tokens) {
        int tokenCount = tokens.size();
        if (tokenCount < 5) {
            return syntaxErrorMessage();
        }
        if (!stringsEqualCaseInsensitively(tokens.get(0), "INTO")) {
            return syntaxErrorMessage();
        }
        String tableName = getTablePath(tokens.get(1));
        File tableToInsert = new File(tableName);
        if (!isExistingTable(tableToInsert)) {
            return error + tableName + " doesn't exist";
        }
        if (!stringsEqualCaseInsensitively(tokens.get(2), "VALUES") || !isOpenBracket(tokens.get(3))) {
            return syntaxErrorMessage();
        }
        List<String> subList = tokens.subList(4, tokenCount - 1);
        DBTable dbTable = this.dbServer.fileToTable(tableName);
        for (int i = 0; i < subList.size(); i++) {
            String s = subList.get(i);
            if (i / 2 == 0 && isComma(s) || i / 2 != 0 && !isComma(s)) {
                return syntaxErrorMessage();
            }
        }
        if (subList.size() != dbTable.getColNames().size()) {
            return error +
        }
    }

    private boolean toSelect(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "SELECT");
    }

    private boolean toUpdate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "UPDATE");
    }

    private boolean toDelete(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "DELETE");
    }

    private boolean toJoin(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "JOIN");
    }

    private boolean endWithSemicolon(List<String> tokens) {
        return tokens.get(tokens.size() - 1).compareTo(";") == 0;
    }

    private boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toUpperCase().compareTo(s2.toUpperCase()) == 0;
    }

    private boolean isReservedKeyword(String s) {
        String[] reservedKeywords = {"USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
                "SELECT", "FROM", "UPDATE", "SET", "WHERE", "DELETE", "JOIN", "ON"};
        return arrayContains(reservedKeywords, s);
    }

    private boolean isPlainText(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(isLetter(c) || isDigit(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean isLetter(Character c) {
        return Character.isUpperCase(c) || Character.isLowerCase(c);
    }

    private boolean isDigit(Character c) {
        int i = Character.getNumericValue(c);
        return 0 <= i && i <= 9;
    }

    private boolean isSpace(String s) {
        return s.compareTo(" ") == 0;
    }

    private boolean isSymbol(String s) {
        String[] symbols = {"!", "#", "$", "%", "&", "(", ")", "*", "+", ",",
                "-", ".", "/", ":", ";", ">", "=", "<", "?", "@", "[", "\\", "]", "^", "_", "`", "{", "}", "~"};
        return arrayContains(symbols, s);
    }

    private boolean isAdd(String s) {
        return stringsEqualCaseInsensitively(s, "ADD");
    }

    private boolean isDrop(String s) {
        return stringsEqualCaseInsensitively(s, "ADD");
    }
    private boolean isComparator(String s) {
        String[] comparators = {"==", ">", "<", ">=", "<=", "!=", "LIKE"};
        return arrayContains(comparators, s);
    }

    private boolean isBoolOperator(String s) {
        String[] boolOperators = {"AND", "OR"};
        return arrayContains(boolOperators, s);
    }

    private boolean isNull(String s) {
        return s.toUpperCase().compareTo("NULL") == 0;
    }

    private boolean isBooleanLiteral(String s) {
        String[] booleanLiterals = {"TRUE", "FALSE"};
        return arrayContains(booleanLiterals, s);
    }

    private boolean arrayContains(String[] array, String s) {
        return Arrays.stream(array).toList().contains(s.toUpperCase());
    }
}
