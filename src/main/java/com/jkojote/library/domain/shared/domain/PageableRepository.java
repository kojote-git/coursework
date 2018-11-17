package com.jkojote.library.domain.shared.domain;

import java.util.List;
import java.util.function.Predicate;

public interface PageableRepository<T extends DomainEntity> extends DomainRepository<T> {

    List<T> findAll(int page, int pageSize);

    List<T> findAll(SqlPageSpecification specification);

    List<T> findAll(int page, int pageSize, Predicate<T> predicate);

}
