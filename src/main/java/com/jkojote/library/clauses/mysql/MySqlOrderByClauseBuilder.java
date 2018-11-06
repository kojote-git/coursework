package com.jkojote.library.clauses.mysql;

import java.util.List;

import static com.jkojote.library.clauses.mysql.MySqlOrderByChain.OrderByTuple;

public class MySqlOrderByClauseBuilder implements OrderByClauseBuilder{

    @Override
    public StringBuilder build(MySqlOrderByChain orderByChain) {
        StringBuilder builder = new StringBuilder();
        List<OrderByTuple> tuples = orderByChain.getTuples();
        if (tuples.size() == 0)
            return builder;
        builder.append("ORDER BY");
        // Just to keep it simple I don't escape any special character
        OrderByTuple tuple = null;
        for (int i = 0; i < tuples.size() - 1; i++) {
            tuple = tuples.get(i);
            builder.append(" ").append(tuple.getAttribute())
                    .append(" ").append(tuple.getSortOrder().asString())
                    .append(",");
        }
        tuple = tuples.get(tuples.size() - 1);
        builder.append(" ").append(tuple.getAttribute())
                .append(" ").append(tuple.getSortOrder().asString());
        return builder;
    }
}
