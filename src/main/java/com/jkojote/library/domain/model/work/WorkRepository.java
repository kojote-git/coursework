package com.jkojote.library.domain.model.work;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface WorkRepository {

    Work findById(long id);

    List<Work> findAll();

    default List<Work> findAll(Predicate<Work> predicate) {
        return findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    boolean exists(Work work);

    long nextId();

    boolean save(Work work);

    boolean update(Work work);

    boolean remove(Work work);

}
