package com.jkojote.library.domain.model.publisher;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.shared.DomainEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Publisher extends DomainEntity {

    private String name;

    private List<Book> books;

    public Publisher(int id, String name, List<Book> books) {
        super(id);
        this.books = new ArrayList<>(books);
        this.name = name;
    }

    public List<Book> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public List<Book> filterBooks(Predicate<Book> book) {
        return books.stream()
                .filter(book)
                .collect(Collectors.toList());
    }
}
