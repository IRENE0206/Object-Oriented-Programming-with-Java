package edu.uob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class Parser {
    private DBServer dbServer;
    private Tokeniser tokeniser;
    private Boolean parsedOK;
    private StringBuilder errorMessage;

    public Parser(DBServer dbServer, String query) {
        this.dbServer = dbServer;
        this.tokeniser = new Tokeniser(query);
        this.parsedOK = false;
        this.errorMessage = new StringBuilder();
        this.errorMessage.append("[ERROR] ");
    }

    public DBCmd parse() {
        return getCommandType();
    }

    public boolean isParsedOK() {
        return this.parsedOK;
    }

    public String getErrorMessage() {
        return this.errorMessage.toString();
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
        this.errorMessage.append(message);
    }

    private boolean invalidDatabaseName(String s) {
        if (!isPlainText(s)) {
            setDatabaseNameErrorMessage(s);
            return true;
        }
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
            attributeList.clear();
            setMissingBracketMessage(")");
            return null;
        }
        if (failToMoveToNextToken()) {
            attributeList.clear();
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
            accumulator.add(getRidOfSingleQuote(token));
            return true;
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

    private String getRidOfSingleQuote(String s) {
        if (s.startsWith("'") && s.endsWith("'")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private boolean toJoin(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "JOIN");
    }

    private boolean isAnd(String s) {
        return stringsEqualCaseInsensitively(s, "AND");
    }

    private boolean isOn(String s) {
        return stringsEqualCaseInsensitively(s, "ON");
    }

    private JoinCMD join() {
        String tableName1 = tokeniser.getToken();
        if (invalidTableName(tableName1)) {
            setTableNameErrorMessage(tableName1);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String and1 = tokeniser.getToken();
        if (!isAnd(and1)) {
            setErrorMessage("Should be 'AND' not " + and1);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName2 = tokeniser.getToken();
        if (invalidTableName(tableName2)) {
            setTableNameErrorMessage(tableName2);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String on = tokeniser.getToken();
        if (!isOn(on)) {
            setErrorMessage("Should be 'ON' not " + on);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String attributeName1 = tokeniser.getToken();
        if (invalidAttributeName(attributeName1)) {
            setInvalidAttributeNameErrorMessage(attributeName1);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String and2 = tokeniser.getToken();
        if (!isAnd(and2)) {
            setErrorMessage("Should be 'AND' not " + and2);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String attributeName2 = tokeniser.getToken();
        if (invalidAttributeName(attributeName2)) {
            setInvalidAttributeNameErrorMessage(attributeName2);
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
        return new JoinCMD(tableName1, tableName2, attributeName1, attributeName2);
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

    private List<Condition> buildCondition() {
        List<Condition> conditions = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        String token1 = tokeniser.getToken();
        int openBracketCount = 0;
        while (tokeniser.hasNextToken()) {
            if (isOpenBracket(token1)) {
                String token2 = tokeniser.getToken();
                if (!isOpenBracket(token2)) {
                    if (invalidAttributeName(token2)) {
                        return null;
                    }
                    Condition atomicCondition = getAtomicCondition();
                    if (atomicCondition == null) {
                        return null;
                    }
                    String token3 = tokeniser.getToken();
                    if (isCloseBracket(token3)) {
                        if (failToMoveToNextToken()) {
                            setLackMoreTokensErrorMessage();
                            return null;
                        }
                        conditions.add(atomicCondition);
                    } else {
                        openBracketCount += 1;
                        operators.push(token1);
                        conditions.add(atomicCondition);
                    }
                } else {
                    openBracketCount += 1;
                    operators.push(token1);
                }
            } else if (isCloseBracket(token1)) {
                openBracketCount -= 1;
                if (openBracketCount < 0) {
                    return null;
                }
                while (!operators.isEmpty() && !isOpenBracket(operators.peek())) {
                    conditions.add(new BoolOperator(operators.pop()));
                }
                operators.pop();
            } else if (isBoolOperator(token1)) {
                if (operators.isEmpty() || operators.contains("(") || hasGreaterPrecedence(token1, operators.peek())) {
                    operators.push(token1);
                } else {
                    while (!operators.isEmpty() && !isOpenBracket(operators.peek())) {
                        conditions.add(new BoolOperator(operators.pop()));
                    }
                    operators.push(token1);
                }
                return null;
            } else {
                if (invalidAttributeName(token1)) {
                    return null;
                }
                Condition atomicCondition = getAtomicCondition();
                if (atomicCondition == null) {
                    return null;
                }
                conditions.add(atomicCondition);
            }
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        while (!operators.isEmpty()) {
            conditions.add(new BoolOperator(operators.pop()));
        }
        return conditions;
    }


    private int getPrecedence(String operator) {
        if (isOr(operator)) {
            return 1;
        } else if (isAnd(operator)) {
            return 2;
        } else if (isOpenBracket(operator)) {
            return 3;
        }
        return 0;
    }

    private boolean hasGreaterPrecedence(String s1, String s2) {
        return getPrecedence(s1) - getPrecedence(s2) > 0;
    }

    private void setBoolOperatorErrorMessage(String s) {
        setErrorMessage(s + " is not a valid [BoolOperator]");
    }

    private boolean isOr(String s) {
        return stringsEqualCaseInsensitively(s, "OR");
    }

    private boolean invalidBoolOperator(String s) {
        if (isBoolOperator(s)) {
            return false;
        }
        setBoolOperatorErrorMessage(s);
        return true;
    }

    private AtomicCondition getAtomicCondition() {
        String attributeName = tokeniser.getToken();
        if (invalidAttributeName(attributeName)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String comparator = tokeniser.getToken();
        if (invalidComparator(comparator)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String value = tokeniser.getToken();
        if (invalidValue(value)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        return new AtomicCondition(comparator, attributeName, getRidOfSingleQuote(value));
    }

    private boolean isComma(String s) {
        return s.compareTo(",") == 0;
    }

    private boolean invalidComparator(String comparator) {
        if (isComparator(comparator)) {
            return false;
        }
        setErrorMessage(comparator + " is not a valid [Comparator]");
        return true;
    }

    private boolean invalidValue(String value) {
        if (isValue(value)) {
            return false;
        }
        setErrorMessage(value + " is not a valid [Value]");
        return true;
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
        System.out.println("q");
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
            System.out.println("w");
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
            System.out.println("e");
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
        setParsedOK();
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
            accumulator.add(getRidOfSingleQuote(value));
            return true;
        } else if (isValue(value) && isComma(currToken)) {
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                accumulator.clear();
                return false;
            }
            accumulator.add(getRidOfSingleQuote(value));
            return getValueList(accumulator);
        } else if (isValue(value)) {
            setMissingBracketMessage(")");
            accumulator.clear();
            return false;
        }
        setErrorMessage(value + " is invalid for [VALUE]");
        accumulator.clear();
        return false;
    }

    private SelectCMD select() {
        if (isWildCard()) {
            System.out.println("WILDCARD");
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return null;
            }
            String from = tokeniser.getToken();
            if (isNotFrom(from)) {
                setErrorMessage(from + " is invalid. Should be FROM");
                return null;
            }
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return null;
            }
            String tableName = tokeniser.getToken();
            if (invalidTableName(tableName)) {
                setTableNameErrorMessage(tableName);
                return null;
            }
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return null;
            }
            String token = tokeniser.getToken();
            if (isSemiColon(token)) {
                if (failToEndWithSemicolonProperly()) {
                    return null;
                }
                setParsedOK();
                return new SelectCMD(tableName);
            } else if (isWhere(token)) {
                if (failToMoveToNextToken()) {
                    setLackMoreTokensErrorMessage();
                    return null;
                }
                System.out.println("WHERE");
                List<Condition> conditions = buildCondition();
                System.out.println("CONDITIONS");
                if (conditions == null) {
                    return null;
                }
                System.out.println("NOTNULL");
                setParsedOK();
                return new SelectCMD(tableName, conditions, true);
            } else {
                setErrorMessage(token + " is not valid keyword");
                return null;
            }
        } else {
            List<String> accumulator = new ArrayList<>();
            if (!getAttributeList(accumulator)) {
                return null;
            }
            String from = tokeniser.getToken();
            if (isNotFrom(from)) {
                setErrorMessage(from + " is invalid. Should be FROM");
                return null;
            }
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return null;
            }
            String tableName = tokeniser.getToken();
            if (invalidTableName(tableName)) {
                setTableNameErrorMessage(tableName);
                return null;
            }
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return null;
            }
            String token = tokeniser.getToken();
            if (isSemiColon(token)) {
                if (failToEndWithSemicolonProperly()) {
                    return null;
                }
                setParsedOK();
                return new SelectCMD(tableName, accumulator);
            } else if (isWhere(token)) {
                if (failToMoveToNextToken()) {
                    setLackMoreTokensErrorMessage();
                    return null;
                }
                List<Condition> conditions = buildCondition();
                if (conditions == null) {
                    return null;
                }
                setParsedOK();
                return new SelectCMD(tableName, accumulator, conditions);
            } else {
                setErrorMessage(token + " is not valid keyword");
                return null;
            }
        }
    }

    private boolean toUpdate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "UPDATE");
    }

    // TODO
    private UpdateCMD update() {
        return null;
    }

    private boolean isWhere(String s) {
        return stringsEqualCaseInsensitively(s, "WHERE");
    }

    private boolean isWildCard() {
        return tokeniser.getToken().compareTo("*") == 0;
    }

    private boolean isNotFrom(String s) {
        return !stringsEqualCaseInsensitively(s, "FROM");
    }

    private boolean toDelete(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "DELETE");
    }

    // TODO
    private DeleteCMD delete() {
        return null;
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
