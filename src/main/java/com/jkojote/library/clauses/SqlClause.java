package com.jkojote.library.clauses;

/**
 * SqlClause represent an optional part of SQL SELECT statement
 * - in this case <b>WHERE</b> and <b>ORDER BY</b> clauses -
 * that give us ability to retrieve data using certain conditions and sort the data
 */
public interface SqlClause {

    /**
     * @return escaped WHERE or/and ORDER BY clauses that can be joined with SELECT statement
     */
    String asString();
}
