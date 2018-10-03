package com.jkojote.library.domain.model.author;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface AuthorRepository {

    Author findById(long id);

    List<Author> findAll();

    default List<Author> findAll(Predicate<Author> predicate) {
        return findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    long nextId();

    boolean exists(Author author);

    boolean save(Author author);

    boolean update(Author author);

    boolean remove(Author author);

}
