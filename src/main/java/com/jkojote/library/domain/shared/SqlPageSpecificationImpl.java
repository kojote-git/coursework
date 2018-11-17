package com.jkojote.library.domain.shared;

import com.jkojote.library.clauses.SqlClause;
import com.jkojote.library.domain.shared.domain.SqlPageSpecification;

import static com.google.common.base.Preconditions.checkNotNull;

public class SqlPageSpecificationImpl implements SqlPageSpecification {

    private SqlClause predicate;

    private int pageSize;

    private int page;

    public SqlPageSpecificationImpl(SqlClause predicate, int pageSize, int page) {
        if (pageSize < 0 || page <= 0)
            throw new IllegalArgumentException("page and pageSize must be positive");
        checkNotNull(predicate);
        this.predicate = predicate;
        this.pageSize = pageSize;
        this.page = page;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public int page() {
        return page;
    }

    @Override
    public SqlClause predicate() {
        return predicate;
    }
}
