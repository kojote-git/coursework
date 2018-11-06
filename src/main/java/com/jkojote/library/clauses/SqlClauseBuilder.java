package com.jkojote.library.clauses;


public interface SqlClauseBuilder {

    SqlComparison where(String attribute);

    SqlOrderByChain orderBy(String attribute, SortOrder order);
}
