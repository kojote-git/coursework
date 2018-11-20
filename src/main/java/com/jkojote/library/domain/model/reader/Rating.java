package com.jkojote.library.domain.model.reader;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.shared.domain.DomainObject;

public final class Rating implements DomainObject {

    private Reader reader;

    private Book book;

    private int rating;

    public Rating(Reader reader, Book book, int rating) {
        if (rating < 0 || rating > 10)
            throw new IllegalArgumentException("rating must be between 0 and 10");
        this.rating = rating;
        this.book = book;
        this.reader = reader;
    }

    public Reader getReader() {
        return reader;
    }

    public Book getBook() {
        return book;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Rating) {
            Rating that = (Rating) obj;
            return  that.reader.equals(reader) &&
                    that.book.equals(book);
        }
        return false;
    }
}
