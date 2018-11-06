package com.jkojote.library.clauses.mysql;

import com.jkojote.library.clauses.SqlClause;

class MySqlClause implements SqlClause {

    private String clause;

    MySqlClause(String clause) {
        this.clause = clause;
    }

    @Override
    public String asString() {
        return clause;
    }
}
