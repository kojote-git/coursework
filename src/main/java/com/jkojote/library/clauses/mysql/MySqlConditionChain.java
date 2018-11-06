package com.jkojote.library.clauses.mysql;

import com.jkojote.library.clauses.*;

import java.util.LinkedList;
import java.util.List;

class MySqlConditionChain implements SqlConditionChain {

    private List<Comparison> comparisons;

    public List<Comparison> getComparisons() {
        return comparisons;
    }

    MySqlConditionChain() {
        this.comparisons = new LinkedList<>();
    }

    @Override
    public SqlComparison and(String attribute) {
        MySqlComparison c = new MySqlComparison(attribute, this, false);
        comparisons.add(new Comparison("AND", c));
        return c;
    }

    @Override
    public SqlComparison or(String attribute) {
        MySqlComparison c = new MySqlComparison(attribute, this, false);
        comparisons.add(new Comparison("OR", c));
        return c;
    }

    @Override
    public SqlOrderByChain orderBy(String attribute, SortOrder order) {
        return new MySqlOrderByChain(attribute, order, this);
    }

    @Override
    public SqlClause build() {
        WhereClauseBuilder clauseBuilder = new MySqlWhereClauseBuilder();
        return new MySqlClause(clauseBuilder.build(this).toString());
    }

    static class Comparison {

        private String before;

        private MySqlComparison comparison;

        Comparison(String before, MySqlComparison comparison) {
            this.before = before;
            this.comparison = comparison;
        }

        public String getBefore() {
            return before;
        }

        public MySqlComparison getComparison() {
            return comparison;
        }
    }
}
