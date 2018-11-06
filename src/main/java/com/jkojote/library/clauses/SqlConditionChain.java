package com.jkojote.library.clauses;


public interface SqlConditionChain {

    SqlComparison and(String attribute);

    SqlComparison or(String attribute);

    SqlOrderByChain orderBy(String attribute, SortOrder order);

    SqlClause build();
}
