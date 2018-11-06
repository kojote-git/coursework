package com.jkojote.library.clauses;

public interface SqlComparison {

    SqlConditionChain eq(String val);

    SqlConditionChain like(String val);

    SqlConditionChain ltOrEq(String val);

    SqlConditionChain gtOrEq(String val);

    SqlConditionChain lt(String val);

    SqlConditionChain notEq(String val);

    SqlConditionChain gt(String val);

    SqlConditionChain lt(long val);

    SqlConditionChain notEq(long val);

    SqlConditionChain gt(long val);

    SqlConditionChain eq(long val);

    SqlConditionChain ltOrEq(long val);

    SqlConditionChain gtOrEq(long val);
}
