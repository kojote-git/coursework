package com.jkojote.library.domain.shared.domain;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface DomainRepository<T extends DomainEntity> {

    T findById(long id);

    List<T> findAll();

    default List<T> findAll(Predicate<T> predicate) {
        return findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    long nextId();

    boolean exists(T entity);

    boolean save(T entity);

    boolean remove(T entity);

    boolean update(T entity);
}
