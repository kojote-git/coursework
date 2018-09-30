package com.jkojote.library.domain.model.book.instance;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.shared.DomainEntity;

import static com.google.common.base.Preconditions.checkNotNull;

public class BookInstance extends DomainEntity {

    private Isbn13 isbn13;

    private Book book;

    private BookFormat format;

    private byte[] file;

    private BookInstance(int id, Book book, Isbn13 isbn13, BookFormat format, byte[] file) {
        super(id);
        this.book = book;
        this.isbn13 = isbn13;
        this.format = format;
        this.file = file;
    }

    public static BookInstance restore(int id, Book book,
                                       Isbn13 isbn13,
                                       BookFormat format,
                                       byte[] file) {
        checkNotNull(book);
        checkNotNull(isbn13);
        checkNotNull(format);
        checkNotNull(file);
        return new BookInstance(id, book, isbn13, format, file);
    }

    public Book getBook() {
        return book;
    }

    public byte[] getFile() {
        return file;
    }

    public Isbn13 getIsbn13() {
        return isbn13;
    }

    public BookFormat getFormat() {
        return format;
    }
}
