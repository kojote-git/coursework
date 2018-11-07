package com.jkojote.library.clauses;

public interface SqlOrderByChain {

    SqlClause build();

    SqlOrderByChain thenOrderBy(String attribute, SortOrder order);

    default SqlOrderByChain thenOrderBy(String attribute) {
        return thenOrderBy(attribute, SortOrder.ASC);
    }
}
