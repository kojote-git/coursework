package com.jkojote.library.clauses.mysql;

import com.jkojote.library.clauses.SqlComparison;
import com.jkojote.library.clauses.SqlConditionChain;

import static com.jkojote.library.clauses.mysql.MySqlConditionChain.Comparison;

class MySqlComparison implements SqlComparison {

    private MySqlConditionChain conditionChain;

    private String op;

    private long longValue;

    private String attribute;

    private String stringValue;

    /**
     * 0 - string
     * 1 - long
     */
    private byte valueType;

    MySqlComparison(String attribute, MySqlConditionChain conditionChain, boolean addToChain) {
        this.attribute = attribute;
        this.conditionChain = conditionChain;
        if (addToChain) {
            this.conditionChain.getComparisons().add(new Comparison("", this));
        }
    }

    public byte getValueType() {
        return valueType;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getOp() {
        return op;
    }

    public long getLongValue() {
        return longValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    @Override
    public SqlConditionChain eq(String val) {
        op = "=";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain like(String val) {
        op = "LIKE";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain ltOrEq(String val) {
        op = "<=";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain gtOrEq(String val) {
        op = ">=";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain lt(String val) {
        op = "<";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain notEq(String val) {
        op = "!=";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain gt(String val) {
        op = ">";
        stringValue = val;
        valueType = 0;
        return conditionChain;
    }

    @Override
    public SqlConditionChain lt(long val) {
        op = "<";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }

    @Override
    public SqlConditionChain notEq(long val) {
        op = "!=";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }

    @Override
    public SqlConditionChain gt(long val) {
        op = ">";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }

    @Override
    public SqlConditionChain eq(long val) {
        op = "=";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }

    @Override
    public SqlConditionChain ltOrEq(long val) {
        op = "<=";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }

    @Override
    public SqlConditionChain gtOrEq(long val) {
        op = ">=";
        longValue = val;
        valueType = 1;
        return conditionChain;
    }
}
