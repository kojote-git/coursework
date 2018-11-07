package com.jkojote.library.domain.model;

import com.jkojote.library.clauses.SortOrder;
import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.clauses.SqlClauseBuilder;
import com.jkojote.library.clauses.mysql.MySqlClauseBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SqlClauseBuilderTest {

    @Test
    public void test() {
        SqlClauseBuilder builder = new MySqlClauseBuilder();
        SqlClause clause1 = builder.where("id").gt(5)
                .orderBy("firstName", SortOrder.DESC)
                .thenOrderBy("lastName", SortOrder.ASC).build();
        SqlClause clause2 = builder.where("id").lt(5)
                .and("title").like("%abc%")
                .or("year").eq(2018)
                .build();
        SqlClause clause3 = builder.orderBy("firstName", SortOrder.DESC)
                .thenOrderBy("lastName", SortOrder.ASC)
                .build();
        // should be escaped
        SqlClause clause4 = builder.where("firstName").eq("De'Loren")
                .orderBy("lastName")
                .build();
        String expected1 = "WHERE id > 5 ORDER BY firstName DESC, lastName ASC";
        String actual1 = clause1.asString();
        String expected2 = "WHERE id < 5 AND title LIKE '%abc%' OR year = 2018";
        String actual2 = clause2.asString();
        String expected3 = "ORDER BY firstName DESC, lastName ASC";
        String actual3 = clause3.asString();
        String expected4 = "WHERE firstName = 'De\\'Loren' ORDER BY lastName ASC";
        String actual4 = clause4.asString();
        assertEquals(expected1, actual1);
        assertEquals(expected2, actual2);
        assertEquals(expected3, actual3);
        assertEquals(expected4, actual4);
    }
}
