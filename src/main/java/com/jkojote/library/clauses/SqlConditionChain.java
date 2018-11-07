package com.jkojote.library.clauses;


public interface SqlConditionChain {

    SqlComparison and(String attribute);

    SqlComparison or(String attribute);

    SqlOrderByChain orderBy(String attribute, SortOrder order);

    default SqlOrderByChain orderBy(String attribute) {
        return orderBy(attribute, SortOrder.ASC);
    }

    SqlClause build();
}
