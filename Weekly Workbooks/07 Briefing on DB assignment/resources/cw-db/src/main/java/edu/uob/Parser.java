package edu.uob;

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
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        if (toUse(firstKeyword)) {
            return use();
        } else if (toCreate(firstKeyword)) {
            return create();
        } else if (toDrop(firstKeyword)) {
            return drop();
        } else if (toAlter(firstKeyword)) {
            return alter();
        } else if (toInsert(firstKeyword)) {
            return insert();
        } else if (toSelect(firstKeyword)) {
            return select();
        } else if (toUpdate(firstKeyword)) {
            return update();
        } else if (toDelete(firstKeyword)) {
            return delete();
        } else if (toJoin(firstKeyword)) {
            return join();
        }
        return null;
    }

    private void setLackMoreTokensErrorMessage() {
        setErrorMessage("More tokens are expected");
    }

    private boolean failToEndWithSemicolonProperly() {
        String token = tokeniser.getToken();
        if (!tokeniser.hasNextToken() && isSemiColon(token)) {
            return false;
        } else if (!tokeniser.hasNextToken()) {
            setErrorMessage("Command should end with ; instead of " + token);
            return true;
        } else if (isSemiColon(token)) {
            setReachSemicolonButNotEndErrorMessage();
            return true;
        }
        setSyntaxErrorMessage();
        return true;
    }

    private boolean toUse(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "USE");
    }

    private void setReachSemicolonButNotEndErrorMessage() {
        setErrorMessage("There should not be more tokens after command ends with ;");
    }

    private UseCMD use() {
        String databaseName = tokeniser.getToken();
        if (invalidDatabaseName(databaseName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new UseCMD(databaseName);
    }

    private void setParsedOK() {
        this.parsedOK = true;
    }

    private void setDatabaseNameErrorMessage(String databaseName) {
        setErrorMessage(databaseName + " is not a valid [DatabaseName]");
    }

    private void setMissingSemiColonErrorMessage() {
        setErrorMessage("Missing ; at the end of command");
    }

    private void setErrorMessage(String message) {
        this.errorMessage += message;
    }

    private boolean invalidDatabaseName(String s) {
        if (isPlainText(s)) {
            return true;
        }
        setDatabaseNameErrorMessage(s);
        return false;
    }

    private boolean isSemiColon(String s) {
        return s.compareTo(";") == 0;
    }

    private CreateCMD create() {
        String databaseOrTable = tokeniser.getToken();
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        if (toDatabase(databaseOrTable)) {
            return createDatabase();
        } else if (toTable(databaseOrTable)) {
            return createTable();
        }
        setSyntaxErrorMessage();
        return null;
    }

    private boolean toDatabase(String databaseOrTable) {
        return stringsEqualCaseInsensitively(databaseOrTable, "DATABASE");
    }

    private boolean toTable(String databaseOrTable) {
        return stringsEqualCaseInsensitively(databaseOrTable, "TABLE");
    }

    private CreateDatabaseCMD createDatabase() {
        String databaseName = tokeniser.getToken();
        if (invalidDatabaseName(databaseName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new CreateDatabaseCMD(databaseName);
    }

    private boolean invalidTableName(String tableName) {
        if (isPlainText(tableName)) {
            return false;
        }
        setTableNameErrorMessage(tableName);
        return true;
    }

    private boolean missingOpenBracket() {
        if (isOpenBracket(tokeniser.getToken())) {
            return false;
        }
        setMissingBracketMessage("(");
        return true;
    }

    private CreateTableCMD createTable() {
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        String currToken = tokeniser.getToken();
        if (isSemiColon(currToken) && !tokeniser.hasNextToken()) {
            setParsedOK();
            return new CreateTableCMD(tableName);
        } else if (missingOpenBracket()) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        List<String> attributeList = new ArrayList<>();
        if (!getAttributeList(attributeList)) {
            return null;
        }
        if (!isCloseBracket(tokeniser.getToken())) {
            setMissingBracketMessage(")");
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new CreateTableCMD(tableName, attributeList);
    }

    private void setSyntaxErrorMessage() {
        setErrorMessage("Invalid syntax");
    }

    private void setMissingBracketMessage(String bracket) {
        setErrorMessage("Missing " + bracket);
    }

    private boolean invalidAttributeName(String s) {
        if (isPlainText(s)) {
            return false;
        } else if (!s.contains(".")) {
            return true;
        }
        int strLength = s.length();
        int index = s.indexOf(".");
        if (index == strLength - 1) {
            return true;
        }
        String tableName = s.substring(0, index);
        String plainText = s.substring(index + 1);
        return invalidTableName(tableName) || invalidPlainText(plainText);
    }

    private boolean invalidPlainText(String plainText) {
        if (isPlainText(plainText)) {
            return false;
        }
        setErrorMessage(plainText + " is not valid [PlainText]");
        return true;
    }

    private void setInvalidAttributeNameErrorMessage(String attributeName) {
        setErrorMessage(attributeName + " is not valid [AttributeName");
    }

    private boolean getAttributeList(List<String> accumulator) {
        String token = tokeniser.getToken();
        if (invalidAttributeName(token)) {
            setInvalidAttributeNameErrorMessage(token);
            accumulator.clear();
            return false;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            accumulator.clear();
            return false;
        }
        String currToken = tokeniser.getToken();
        if (!isComma(currToken)) {
            accumulator.add(token);
            return false;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            accumulator.clear();
            return false;
        }
        accumulator.add(token);
        return getAttributeList(accumulator);
    }

    private void setTableNameErrorMessage(String tableName) {
        setErrorMessage(tableName + " is not a valid [TableName]");
    }

    private boolean toJoin(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "JOIN");
    }

    private JoinCMD join() {
        String tableName1 = tokeniser.getToken();
        if (!isValidTableName(tableName1)) {
            setTableNameErrorMessage(tableName1);
            return null;
        }
        if (failToMoveToNextToken()) {
            return null;
        }
        String and = tokeniser.getToken();
        if (!stringsEqualCaseInsensitively(and, "AND")) {
            setErrorMessage("Should be 'AND' not " + and);
            return null;
        }
        if (failToMoveToNextToken()) {
            return null;
        }
        String tableName2 = tokeniser.getToken();
        if (!isValidTableName(tableName2)) {
            setTableNameErrorMessage(tableName2);
            return null;
        }
        if (failToMoveToNextToken()) {
            return null;
        }
        String on = tokeniser.getToken();
        if (!stringsEqualCaseInsensitively(on, "AND")) {
            setErrorMessage("Should be 'AND' not " + and);
            return null;
        }
    }

    private boolean failToMoveToNextToken() {
        if (tokeniser.hasNextToken()) {
            tokeniser.nextToken();
            return false;
        }
        return true;
    }


    private AlterCMD alter() {
        String secondKeyword = tokeniser.getToken();
        if (!toTable(secondKeyword)) {
            setErrorMessage(secondKeyword + " is not valid syntax");
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String alterType = tokeniser.getToken();
        if(!isAdd(alterType) && !isDrop(alterType)) {
            setErrorMessage(alterType + " is not valid [AlterationType]");
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String attributeName = tokeniser.getToken();
        if (invalidAttributeName(attributeName)) {
            setInvalidAttributeNameErrorMessage(attributeName);
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        if (isAdd(alterType)) {
            return new AlterAddCMD(tableName, alterType, attributeName);
        } else {
            return new AlterDropCMD(tableName, alterType, attributeName);
        }
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

    private boolean toCreate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "CREATE");
    }

    private boolean toActOnTable(String secondKeyword) {
        return stringsEqualCaseInsensitively(secondKeyword, "TABLE");
    }

    private boolean toDrop(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "DROP");
    }

    private DropCMD drop() {
        String databaseOrTable = tokeniser.getToken();
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        if (toDatabase(databaseOrTable)) {
            return dropDatabase();
        } else if (toTable(databaseOrTable)) {
            return dropTable();
        } else {
            setErrorMessage(databaseOrTable + " is not valid keyword for Drop");
            return null;
        }
    }

    private DropDatabaseCMD dropDatabase() {
        String databaseName = tokeniser.getToken();
        if (invalidDatabaseName(databaseName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new DropDatabaseCMD(databaseName);
    }

    private DropTableCMD dropTable() {
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new DropTableCMD(tableName);
    }

    private boolean toAlter(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "ALTER");
    }

    private boolean toInsert(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "INSERT");
    }

    private InsertCMD insert() {
        String secondKeyword = tokeniser.getToken();
        if(!stringsEqualCaseInsensitively(secondKeyword, "INTO")) {
            setErrorMessage(secondKeyword + " is not invalid keyword 'INTO'");
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String thirdKeyword = tokeniser.getToken();
        if(!stringsEqualCaseInsensitively(thirdKeyword, "VALUES")) {
            setErrorMessage(secondKeyword + " is not invalid keyword 'VALUES'");
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        if (missingOpenBracket()) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        List<String> accumulator = new ArrayList<>();
        if (!getValueList(accumulator)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            failToEndWithSemicolonProperly();
            return null;
        }
        return new InsertCMD(tableName, accumulator);
    }

    private boolean toSelect(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "SELECT");
    }

    private boolean getValueList(List<String> accumulator) {
        String value = tokeniser.getToken();
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            accumulator.clear();
            return false;
        }
        String currToken = tokeniser.getToken();
        if (isValue(value) && isCloseBracket(currToken)) {
            accumulator.add(value);
            return true;
        } else if (isValue(value) && isComma(currToken)) {
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                accumulator.clear();
                return false;
            }
            return getValueList(accumulator);
        } else if (isValue(value)) {
            setMissingBracketMessage(")");
            accumulator.clear();
            return false;
        }
        accumulator.clear();
        return false;
    }

    private boolean toUpdate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "UPDATE");
    }

    private boolean toDelete(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "DELETE");
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

    private boolean isLetter(String s) {
        return s.length() == 1 && isLetter(s.charAt(0));
    }

    private boolean isDigit(Character c) {
        int i = Character.getNumericValue(c);
        return 0 <= i && i <= 9;
    }

    private boolean isDigit(String s) {
        return s.length() == 1 && isDigit(s.charAt(0));
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

    private boolean isNull(String s) {
        return stringsEqualCaseInsensitively(s, "NULL");
    }

    private boolean isBooleanLiteral(String s) {
        return stringsEqualCaseInsensitively(s, "TRUE") || stringsEqualCaseInsensitively(s, "FALSE");
    }

    private boolean arrayContains(String[] array, String s) {
        return Arrays.stream(array).toList().contains(s.toUpperCase());
    }

    private boolean isWildAttribList() {
        return
    }

    private boolean isBoolOperator(String s) {
        return stringsEqualCaseInsensitively(s, "AND") || stringsEqualCaseInsensitively(s, "OR");
    }

    private boolean isComparator(String s) {
        String[] comparators = {"==", ">", "<", ">=", "<=", "!=", "LIKE"};
        return arrayContains(comparators, s.toUpperCase());
    }

    private boolean isCharLiteral(String s) {
        return isSpace(s) || isLetter(s) || isSymbol(s) || isDigit(s);
    }

    private boolean isDigitalSequence(String s) {
        return isDigit(s) ||
                (s.length() > 1 && isDigit(s.charAt(0)) && isDigitalSequence(s.substring(1)));
    }

    private boolean isIntegerLiteral(String s) {
        return isDigitalSequence(s) ||
                (s.length() > 1 && isPlusOrMinusSign(s.charAt(0)) && isDigitalSequence(s.substring(1)));
    }

    private boolean isPlusOrMinusSign(char c) {
        return c == '+' || c == '-';
    }

    private boolean isFloatLiteral(String s) {
        if (!s.contains(".") || s.length() < 3) {
            return false;
        }
        int length = s.length();
        int index = s.indexOf(".");
        if (index == length - 1) {
            return false;
        }
        String substring1 = s.substring(0, index);
        String substring2 = s.substring(index + 1, length);
        return isDigitalSequence(substring2) &&
                (isDigitalSequence(substring1) ||
                        (substring1.length() > 1 && isPlusOrMinusSign(substring1.charAt(0))
                                && isDigitalSequence(substring1.substring(1))));
    }

    private boolean isStringLiteral(String s) {
        return s.isEmpty() || isCharLiteral(s) ||
                (isStringLiteral(s.substring(0, s.length() - 1))
                        && isCharLiteral(s.substring(s.length() - 1)));
    }

    private boolean isValue(String s) {
        if (isNull(s) || isIntegerLiteral(s) || isFloatLiteral(s) || isBooleanLiteral(s)) {
            return true;
        } else if (s.length() >= 2) {
            return s.startsWith("'") && s.endsWith("'") && isStringLiteral(s.substring(1, s.length() - 1));
        }
        return false;
    }
}
