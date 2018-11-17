package com.jkojote.library.domain.shared.domain;

import com.jkojote.library.clauses.SqlClause;

public interface SqlPageSpecification {

    int pageSize();

    int page();

    SqlClause predicate();
}
