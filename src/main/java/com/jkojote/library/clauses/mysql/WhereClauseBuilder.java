package com.jkojote.library.clauses.mysql;

public interface WhereClauseBuilder {

    StringBuilder build(MySqlConditionChain conditionChain);
}
