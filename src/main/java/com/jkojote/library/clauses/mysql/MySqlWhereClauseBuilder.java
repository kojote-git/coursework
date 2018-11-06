package com.jkojote.library.clauses.mysql;

import static com.jkojote.library.clauses.mysql.MySqlConditionChain.Comparison;

public class MySqlWhereClauseBuilder implements WhereClauseBuilder {

    @Override
    public StringBuilder build(MySqlConditionChain conditionChain) {
        StringBuilder builder = new StringBuilder();
        if (conditionChain.getComparisons().size() == 0)
            return builder;
        builder.append("WHERE");
        // Just to keep it simple, I don't escape any special character
        for (Comparison c : conditionChain.getComparisons()) {
            MySqlComparison mComp = c.getComparison();
            if (!c.getBefore().equals(""))
                builder.append(" ");
            builder.append(c.getBefore()).append(" ")
                    .append(mComp.getAttribute()).append(" ")
                    .append(mComp.getOp()).append(" ");
            if (mComp.getValueType() == 0) {
                builder.append("'").append(mComp.getStringValue()).append("'");
            } else {
                builder.append(mComp.getLongValue());
            }
        }
        return builder;
    }
}
