package com.jkojote.library.domain.model.publisher;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.shared.DomainEntity;
import com.jkojote.library.domain.shared.EntityArrayList;
import com.jkojote.library.domain.shared.EntityList;
import com.jkojote.library.domain.shared.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Publisher extends DomainEntity {

    private String name;

    private EntityList<Book> books;

    public Publisher(int id, String name, EntityList<Book> books) {
        super(id);
        this.books = books;
        this.name = name;
    }

    public EntityList<Book> getBooks() {
        return Utils.unmodifiableEntityList(books);
    }

    public List<Book> filterBooks(Predicate<Book> book) {
        return books.stream()
                .filter(book)
                .collect(Collectors.toList());
    }
}
