package com.jkojote.library.clauses.mysql;

import com.jkojote.library.clauses.SortOrder;
import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.clauses.SqlOrderByChain;

import java.util.LinkedList;
import java.util.List;

class MySqlOrderByChain implements SqlOrderByChain {
    private MySqlConditionChain conditionChain;

    private List<OrderByTuple> tuples;

    MySqlOrderByChain(String attribute, SortOrder order) {
        this.tuples = new LinkedList<>();
        this.tuples.add(new OrderByTuple(attribute, order));
    }

    MySqlOrderByChain(String attribute, SortOrder order,
                      MySqlConditionChain conditionChain) {
        this.conditionChain = conditionChain;
        this.tuples = new LinkedList<>();
        this.tuples.add(new OrderByTuple(attribute, order));
    }

    public List<OrderByTuple> getTuples() {
        return tuples;
    }

    @Override
    public SqlClause build() {
        OrderByClauseBuilder orderByClauseBuilder = ClauseBuilders.getOrderByClauseBuilder();
        WhereClauseBuilder whereClauseBuilder = ClauseBuilders.getWhereClauseBuilder();
        if (conditionChain == null) {
            return new MySqlClause(orderByClauseBuilder.build(this).toString());
        } else {
            StringBuilder where = whereClauseBuilder.build(conditionChain);
            where.append(" ").append(orderByClauseBuilder.build(this).toString());
            return new MySqlClause(where.toString());
        }
    }

    @Override
    public SqlOrderByChain thenOrderBy(String attribute, SortOrder order) {
        this.tuples.add(new OrderByTuple(attribute, order));
        return this;
    }

    static class OrderByTuple {

        private String attribute;

        private SortOrder sortOrder;

        public OrderByTuple(String attribute, SortOrder sortOrder) {
            this.attribute = attribute;
            this.sortOrder = sortOrder;
        }

        public SortOrder getSortOrder() {
            return sortOrder;
        }

        public String getAttribute() {
            return attribute;
        }
    }
}
