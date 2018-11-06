package com.jkojote.library.clauses.mysql;

import com.jkojote.library.clauses.SortOrder;
import com.jkojote.library.clauses.SqlClauseBuilder;
import com.jkojote.library.clauses.SqlComparison;
import com.jkojote.library.clauses.SqlOrderByChain;

public class MySqlClauseBuilder implements SqlClauseBuilder {

    @Override
    public SqlComparison where(String attribute) {
        return new MySqlComparison(attribute, new MySqlConditionChain(), true);
    }

    @Override
    public SqlOrderByChain orderBy(String attribute, SortOrder order) {
        return new MySqlOrderByChain(attribute, order);
    }
}
