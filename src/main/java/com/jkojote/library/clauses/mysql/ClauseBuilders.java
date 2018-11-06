package com.jkojote.library.clauses.mysql;

public final class ClauseBuilders {

    private static final OrderByClauseBuilder ORDER_BY_CLAUSE_BUILDER;

    private static final WhereClauseBuilder WHERE_CLAUSE_BUILDER;

    static {
        ORDER_BY_CLAUSE_BUILDER = new MySqlOrderByClauseBuilder();
        WHERE_CLAUSE_BUILDER = new MySqlWhereClauseBuilder();
    }

    public static OrderByClauseBuilder getOrderByClauseBuilder() {
        return ORDER_BY_CLAUSE_BUILDER;
    }

    public static WhereClauseBuilder getWhereClauseBuilder() {
        return WHERE_CLAUSE_BUILDER;
    }
}
