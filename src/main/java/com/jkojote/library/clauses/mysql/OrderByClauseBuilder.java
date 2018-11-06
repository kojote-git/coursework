package com.jkojote.library.clauses.mysql;

public interface OrderByClauseBuilder {

    StringBuilder build(MySqlOrderByChain orderByChain);

}
