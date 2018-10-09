package com.jkojote.library.domain.model.publisher;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.domain.shared.Utils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Publisher extends DomainEntity {

    private String name;

    private DomainList<Book> books;

    public Publisher(long id, String name, DomainList<Book> books) {
        super(id);
        this.books = books;
        this.name = name;
    }

    public DomainList<Book> getBooks() {
        return Utils.unmodifiableDomainList(books);
    }

    public List<Book> filterBooks(Predicate<Book> book) {
        return books.stream()
                .filter(book)
                .collect(Collectors.toList());
    }
}
