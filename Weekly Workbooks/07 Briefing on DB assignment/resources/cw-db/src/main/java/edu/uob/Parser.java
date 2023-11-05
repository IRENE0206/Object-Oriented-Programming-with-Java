package edu.uob;

import java.util.*;

public class Parser {
    private final Tokeniser tokeniser;
    private Boolean parsedOK;
    private final StringBuilder errorMessage;

    public Parser(String query) {
        this.tokeniser = new Tokeniser(query);
        this.parsedOK = false;
        this.errorMessage = new StringBuilder();
        this.errorMessage.append("[ERROR] ");
    }

    public DBCmd parseCommand() {
        return getCommandType();
    }

    public boolean getParsedOk() {
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
            return useDatabase();
        } else if (toCreate(firstKeyword)) {
            return createDatabaseOrTable();
        } else if (toDrop(firstKeyword)) {
            return dropDatabaseOrTable();
        } else if (toAlter(firstKeyword)) {
            return alterTable();
        } else if (toInsert(firstKeyword)) {
            return insertTableRow();
        } else if (toSelect(firstKeyword)) {
            return selectFromTable();
        } else if (toUpdate(firstKeyword)) {
            return updateTable();
        } else if (toDelete(firstKeyword)) {
            return deleteTableRow();
        } else if (toJoin(firstKeyword)) {
            return joinTwoTables();
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
        setErrorMessage("Invalid syntax");
        return true;
    }

    private boolean toUse(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "USE");
    }

    private void setReachSemicolonButNotEndErrorMessage() {
        setErrorMessage("There should not be more tokens after command ends with ;");
    }

    private UseCMD useDatabase() {
        String databaseName = tokeniser.getToken();
        if (invalidDatabaseName(databaseName)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new UseCMD(databaseName);
    }

    private void setParsedOK() {
        this.parsedOK = true;
    }

    private void setDBNameErrorMessage(String databaseName) {
        setErrorMessage(databaseName + " is not a valid [DatabaseName]");
    }

    private void setMissingSemiColonErrorMessage() {
        setErrorMessage("Missing ; at the end of command");
    }

    private void setErrorMessage(String message) {
        this.errorMessage.append(message);
    }

    private boolean invalidDatabaseName(String s) {
        if (!isPlainText(s) || isReservedKeyword(s)) {
            setDBNameErrorMessage(s);
            return true;
        }
        return false;
    }

    private boolean isSemiColon(String s) {
        return s.compareTo(";") == 0;
    }

    private DBCmd createDatabaseOrTable() {
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
        setErrorMessage(databaseOrTable + " is not valid keyword. Should be 'DATABASE' or 'TABLE'");
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
        } else if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new CreateDatabaseCMD(databaseName);
    }

    private boolean invalidTableName(String tableName) {
        if (isPlainText(tableName) && !isReservedKeyword(tableName)) {
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
        } else if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        String currToken = tokeniser.getToken();
        if (isSemiColon(currToken) && !tokeniser.hasNextToken()) {
            setParsedOK();
            return new CreateTableCMD(tableName);
        }
        return createTableWithAttributeList(tableName);
    }

    private CreateTableCMD createTableWithAttributeList(String tableName) {
        if (missingOpenBracket()) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        List<String> attributeList = new ArrayList<>();
        if (!getAttributeList(attributeList)) {
            return null;
        } else if (!isCloseBracket(tokeniser.getToken())) {
            attributeList.clear();
            setMissingBracketMessage(")");
            return null;
        } else if (failToMoveToNextToken()) {
            attributeList.clear();
            setMissingSemiColonErrorMessage();
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        return new CreateTableCMD(tableName, attributeList);
    }

    private void setMissingBracketMessage(String bracket) {
        setErrorMessage("Missing " + bracket);
    }

    private boolean invalidAttributeName(String s) {
        if (isPlainText(s) && !isReservedKeyword(s)) {
            return false;
        } else if (!s.contains(".")) {
            return true;
        }
        int strLength = s.length();
        int index = s.indexOf('.');
        if (index == strLength - 1) {
            return true;
        }
        String tableName = s.substring(0, index);
        String plainText = s.substring(index + 1);
        return invalidTableName(tableName) || invalidPlainText(plainText);
    }

    private boolean invalidPlainText(String plainText) {
        if (isPlainText(plainText) && !isReservedKeyword(plainText)) {
            return false;
        }
        setErrorMessage(plainText + " is not valid [PlainText]");
        return true;
    }

    private boolean getAttributeList(List<String> accumulator) {
        String token = tokeniser.getToken();
        if (invalidAttributeName(token)) {
            accumulator.clear();
            return false;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            accumulator.clear();
            return false;
        }
        String currToken = tokeniser.getToken();
        if (!isComma(currToken)) {
            accumulator.add(removeSingleQuotes(token));
            return true;
        } else if (failToMoveToNextToken()) {
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

    private String removeSingleQuotes(String s) {
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

    private JoinCMD joinTwoTables() {
        String tableName1 = getFirstTableName();
        if (tableName1 == null) {
            return null;
        }
        String tableName2 = getSecondTableName();
        if (tableName2 == null) {
            return null;
        }
        String attributeName1 = getFirstAttributeList();
        String and2 = tokeniser.getToken();
        if (!isAnd(and2)) {
            setErrorMessage("Should be 'AND' not " + and2);
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String attributeName2 = getSecondAttributeList();
        setParsedOK();
        return new JoinCMD(tableName1, tableName2, attributeName1, attributeName2);
    }

    private String getFirstTableName() {
        String tableName1 = tokeniser.getToken();
        if (invalidTableName(tableName1)) {
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
        return tableName1;
    }

    private String getSecondTableName() {
        String tableName2 = tokeniser.getToken();
        if (invalidTableName(tableName2)) {
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
        return tableName2;
    }

    private String getFirstAttributeList() {
        String attributeName1 = tokeniser.getToken();
        if (invalidAttributeName(attributeName1)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        return attributeName1;
    }

    private String getSecondAttributeList() {
        String attributeName2 = tokeniser.getToken();
        if (invalidAttributeName(attributeName2)) {
            return null;
        }
        if (failToMoveToNextToken()) {
            setMissingSemiColonErrorMessage();
            return null;
        }
        if (failToEndWithSemicolonProperly()) {
            return null;
        }
        return attributeName2;
    }

    private boolean failToMoveToNextToken() {
        if (tokeniser.hasNextToken()) {
            tokeniser.nextToken();
            return false;
        }
        return true;
    }

    private boolean failToHandleKeyword(String secondKeyword) {
        if (!toTable(secondKeyword)) {
            setErrorMessage(secondKeyword + " is not valid syntax");
            return true;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private boolean failToHandleTableName(String tableName) {
        if (invalidTableName(tableName)) {
            return true;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private boolean failToHandleAlterType(String alterType) {
        if (!isAdd(alterType) && !isDrop(alterType)) {
            setErrorMessage(alterType + " is not valid [AlterationType]");
            return true;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private DBCmd alterTable() {
        String secondKeyword = tokeniser.getToken();
        if (failToHandleKeyword(secondKeyword)) {
            return null;
        }
        String tableName = tokeniser.getToken();
        if (failToHandleTableName(tableName)) {
            return null;
        }
        String alterType = tokeniser.getToken();
        if (failToHandleAlterType(alterType)) {
            return null;
        }
        String attributeName = tokeniser.getToken();
        if (failToHandleAttributeName(attributeName)) {
            return null;
        } else if (failToEndWithSemicolonProperly()) {
            return null;
        }
        setParsedOK();
        if (isAdd(alterType)) {
            return new AlterAddCMD(tableName, attributeName);
        }
        return new AlterDropCMD(tableName, attributeName);
    }

    private List<Condition> buildCondition() {
        List<Condition> conditions = new ArrayList<>();
        Stack<String> operators = new Stack<>();
        int openBracketCount = 0;
        while (!isSemiColon(tokeniser.getToken())) {
            String token1 = tokeniser.getToken();
            if (isOpenBracket(token1)) {
                openBracketCount += 1;
                if (failToMoveToNextToken()) {
                    setLackMoreTokensErrorMessage();
                    return null;
                }
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
                        openBracketCount -= 1;
                        if (failToMoveToNextToken()) {
                            setLackMoreTokensErrorMessage();
                            return null;
                        }
                        conditions.add(atomicCondition);
                    } else {
                        operators.push(token1);
                        conditions.add(atomicCondition);
                    }
                } else {
                    operators.push(token1);
                }
            } else if (isCloseBracket(token1)) {
                openBracketCount -= 1;
                if (openBracketCount < 0) {
                    return null;
                }
                if (failToMoveToNextToken()) {
                    setLackMoreTokensErrorMessage();
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
                if (failToMoveToNextToken()) {
                    setLackMoreTokensErrorMessage();
                    return null;
                }
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
        if (!conditions.isEmpty()) {
            return conditions;
        }
        return null;
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

    private boolean isOr(String s) {
        return stringsEqualCaseInsensitively(s, "OR");
    }

    private AtomicCondition getAtomicCondition() {
        String attributeName = tokeniser.getToken();
        if (invalidAttributeName(attributeName)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String comparator = tokeniser.getToken();
        if (invalidComparator(comparator)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String value = tokeniser.getToken();
        if (invalidValue(value)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        boolean isStringLiteral = isStringLiteral(value);
        return new AtomicCondition(comparator, attributeName, removeSingleQuotes(value), isStringLiteral);
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

    private DBCmd dropDatabaseOrTable() {
        String databaseOrTable = tokeniser.getToken();
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        } else if (toDatabase(databaseOrTable)) {
            return dropDatabase();
        } else if (toTable(databaseOrTable)) {
            return dropTable();
        }
        setErrorMessage(databaseOrTable + " is not valid keyword for Drop");
        return null;
    }

    private DropDatabaseCMD dropDatabase() {
        String databaseName = tokeniser.getToken();
        if (invalidDatabaseName(databaseName)) {
            return null;
        } else if (failToMoveToNextToken()) {
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
        } else if (failToMoveToNextToken()) {
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

    private InsertCMD insertTableRow() {
        String secondKeyword = tokeniser.getToken();
        if (!stringsEqualCaseInsensitively(secondKeyword, "INTO")) {
            setErrorMessage(secondKeyword + " is not invalid keyword 'INTO'");
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (failToHandleTableName(tableName)) {
            return null;
        }
        String thirdKeyword = tokeniser.getToken();
        if (!stringsEqualCaseInsensitively(thirdKeyword, "VALUES")) {
            setErrorMessage(secondKeyword + " is not invalid keyword 'VALUES'");
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        } else if (missingOpenBracket()) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        List<String> accumulator = new ArrayList<>();
        if (!getValueList(accumulator)) {
            return null;
        } else if (failToMoveToNextToken()) {
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
            accumulator.add(removeSingleQuotes(value));
            return true;
        } else if (isValue(value) && isComma(currToken)) {
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                accumulator.clear();
                return false;
            }
            accumulator.add(removeSingleQuotes(value));
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

    private SelectCMD selectWildCard() {
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String from = tokeniser.getToken();
        if (isNotFrom(from)) {
            setErrorMessage(from + " is invalid. Should be FROM");
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (failToHandleTableName(tableName)) {
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
            List<Condition> conditions = buildCondition();
            if (conditions == null) {
                return null;
            }
            setParsedOK();
            return new SelectCMD(tableName, conditions, true);
        }
        setErrorMessage(token + " is not valid keyword");
        return null;
    }

    private SelectCMD selectFromTable() {
        if (isWildCard()) {
            return selectWildCard();
        }
        return selectCertainAttributes();
    }

    private SelectCMD selectCertainAttributes() {
        List<String> accumulator = new ArrayList<>();
        if (!getAttributeList(accumulator)) {
            return null;
        }
        String from = tokeniser.getToken();
        if (isNotFrom(from)) {
            setErrorMessage(from + " is invalid. Should be FROM");
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (failToHandleTableName(tableName)) {
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
        }
        setErrorMessage(token + " is not valid keyword");
        return null;
    }

    private boolean toUpdate(String firstKeyword) {
        return stringsEqualCaseInsensitively(firstKeyword, "UPDATE");
    }

    private UpdateCMD updateTable() {
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String token1 = tokeniser.getToken();
        if (!isSet(token1)) {
            setErrorMessage(token1 + " is not valid. Should be SET");
            return null;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        Map<String, String> nameValueList = getNameValueList();
        if (nameValueList == null) {
            return null;
        }
        List<Condition> conditions = buildCondition();
        if (conditions == null) {
            return null;
        }
        setParsedOK();
        return new UpdateCMD(tableName, nameValueList, conditions);
    }

    private boolean isWhere(String s) {
        return stringsEqualCaseInsensitively(s, "WHERE");
    }

    private Map<String, String> getNameValueList() {
        Map<String, String> accumulator = new HashMap<>();
        if (getNameValuePair(accumulator)) {
            return accumulator;
        }
        return null;
    }

    private boolean failToHandleEqualSign(String equalSign) {
        if (equalSign.compareTo("=") != 0) {
            setErrorMessage(equalSign + " is invalid. Should be '='");
            return true;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private boolean getNameValuePair(Map<String, String> accumulator) {
        String attributeName = tokeniser.getToken();
        if (failToHandleAttributeName(attributeName)) {
            return false;
        }
        String equalSign = tokeniser.getToken();
        if (failToHandleEqualSign(equalSign)) {
            return false;
        }
        String value = tokeniser.getToken();
        if (failToHandleValue(value)) {
            return false;
        }
        String next = tokeniser.getToken();
        if (isComma(next)) {
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return false;
            }
            accumulator.put(attributeName, removeSingleQuotes(value));
            return getNameValuePair(accumulator);
        } else if (isWhere(next)) {
            if (failToMoveToNextToken()) {
                setLackMoreTokensErrorMessage();
                return false;
            }
            accumulator.put(attributeName, removeSingleQuotes(value));
            return true;
        } else {
            setErrorMessage(next + " is not valid keyword");
            return false;
        }
    }

    private boolean failToHandleAttributeName(String attributeName) {
        if (invalidAttributeName(attributeName)) {
            return true;
        }
        if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private boolean failToHandleValue(String value) {
        if (!isValue(value)) {
            setErrorMessage(value + " is not valid [Value]");
            return true;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return true;
        }
        return false;
    }

    private boolean isSet(String s) {
        return stringsEqualCaseInsensitively(s, "SET");
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

    private DeleteCMD deleteTableRow() {
        String from = tokeniser.getToken();
        if (isNotFrom(from)) {
            setErrorMessage(from + " is not valid keyword. Should be 'FROM'");
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String tableName = tokeniser.getToken();
        if (invalidTableName(tableName)) {
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        String where = tokeniser.getToken();
        if (!isWhere(where)) {
            setWhereErrorMessage(where);
            return null;
        } else if (failToMoveToNextToken()) {
            setLackMoreTokensErrorMessage();
            return null;
        }
        List<Condition> conditions = buildCondition();
        if (conditions == null) {
            return null;
        }
        setParsedOK();
        return new DeleteCMD(tableName, conditions);
    }

    private boolean stringsEqualCaseInsensitively(String s1, String s2) {
        return s1.toUpperCase().compareTo(s2.toUpperCase()) == 0;
    }

    private void setWhereErrorMessage(String s) {
        setErrorMessage(s + " is not valid keyword. Should be 'WHERE'");
    }

    private boolean isReservedKeyword(String s) {
        String[] reservedKeywords = {"USE", "CREATE", "DATABASE", "TABLE", "DROP", "ALTER", "INSERT", "INTO", "VALUES",
            "SELECT", "FROM", "UPDATE", "SET", "WHERE", "DELETE", "JOIN", "ON",
            "AND", "ADD", "TRUE", "FALSE", "OR", "LIKE"};
        return stringArrayContainsString(reservedKeywords, s);
    }

    private boolean isPlainText(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!(characterIsLetter(c) || isDigit(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean characterIsLetter(Character c) {
        return ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z');
    }

    private boolean isLetter(String s) {
        return s.length() == 1 && characterIsLetter(s.charAt(0));
    }

    private boolean isDigit(Character c) {
        return '0' <= c && c <= '9';
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
        return stringArrayContainsString(symbols, s);
    }

    private boolean isAdd(String s) {
        return stringsEqualCaseInsensitively(s, "ADD");
    }

    private boolean isDrop(String s) {
        return stringsEqualCaseInsensitively(s, "DROP");
    }

    private boolean isNull(String s) {
        return stringsEqualCaseInsensitively(s, "NULL");
    }

    private boolean isBooleanLiteral(String s) {
        return stringsEqualCaseInsensitively(s, "TRUE") || stringsEqualCaseInsensitively(s, "FALSE");
    }

    private boolean stringArrayContainsString(String[] array, String s) {
        return Arrays.stream(array).toList().contains(s.toUpperCase());
    }

    private boolean isBoolOperator(String s) {
        return stringsEqualCaseInsensitively(s, "AND") || stringsEqualCaseInsensitively(s, "OR");
    }

    private boolean isComparator(String s) {
        String[] comparators = {"==", ">", "<", ">=", "<=", "!=", "LIKE"};
        return stringArrayContainsString(comparators, s);
    }

    private boolean isCharLiteral(String s) {
        return isSpace(s) || isLetter(s) || isSymbol(s) || isDigit(s);
    }

    private boolean isDigitalSequence(String s) {
        return isDigit(s)
                ||
                (s.length() > 1 && isDigit(s.charAt(0)) && isDigitalSequence(s.substring(1)));
    }

    private boolean isIntegerLiteral(String s) {
        return isDigitalSequence(s)
                ||
                (s.length() > 1 && isPlusOrMinusSign(s.charAt(0)) && isDigitalSequence(s.substring(1)));
    }

    private boolean isPlusOrMinusSign(char c) {
        return c == '+' || c == '-';
    }

    private boolean isFloatLiteral(String s) {
        if (!s.contains(".") || s.length() <= 2) {
            return false;
        }
        int length = s.length();
        int index = s.indexOf('.');
        if (index == length - 1) {
            return false;
        }
        String substring1 = s.substring(0, index);
        String substring2 = s.substring(index + 1, length);
        return isDigitalSequence(substring2) && (isDigitalSequence(substring1) || isNegativeFloat(substring1));
    }

    private boolean isNegativeFloat(String stringBeforePoint) {
        return stringBeforePoint.length() > 1 && isPlusOrMinusSign(stringBeforePoint.charAt(0))
            && isDigitalSequence(stringBeforePoint.substring(1));
    }

    private boolean isStringLiteral(String s) {
        return s.isEmpty() || isCharLiteral(s)
                ||
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
