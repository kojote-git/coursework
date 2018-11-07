package com.jkojote.library.domain.shared.domain;

import com.jkojote.library.clauses.SqlClause;

import java.util.List;

public interface FilteringAndSortingRepository<T extends DomainEntity>
extends DomainRepository<T> {

    List<T> findAll(SqlClause clause);
}
