package com.jkojote.library.domain.model.book.instance;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.files.FileInstance;


public class BookInstance extends DomainEntity {

    private Isbn13 isbn13;

    private Book book;

    private BookFormat format;

    private FileInstance file;

    public BookInstance(long id, Book book, Isbn13 isbn13, BookFormat format, FileInstance file) {
        super(id);
        this.book = book;
        this.isbn13 = isbn13;
        this.format = format;
        this.file = file;
    }

    public BookInstance(long id, Book book, Isbn13 isbn13, BookFormat format) {
        super(id);
        this.book = book;
        this.isbn13 = isbn13;
        this.format = format;
    }

    public Book getBook() {
        return book;
    }

    public void setFile(FileInstance file) {
        this.file = file;
    }

    public FileInstance getFile() {
        return file;
    }

    public Isbn13 getIsbn13() {
        return isbn13;
    }

    public BookFormat getFormat() {
        return format;
    }
}
