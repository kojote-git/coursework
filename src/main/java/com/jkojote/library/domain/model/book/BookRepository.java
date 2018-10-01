package com.jkojote.library.domain.model.book;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface BookRepository {

    Book findById(long id);

    List<Book> findAll();

    default List<Book> findAll(Predicate<Book> predicate) {
        return findAll().stream()
                .filter(predicate)
                .collect(Collectors.toList());
    }

    boolean exists(Book book);

    boolean save(Book book);

    boolean update(Book update);

    boolean remove(Book book);
}
