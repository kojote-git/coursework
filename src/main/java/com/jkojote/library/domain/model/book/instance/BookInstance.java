package com.jkojote.library.domain.model.book.instance;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.isbn.Isbn13;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.Required;
import com.jkojote.library.files.FileInstance;
import com.jkojote.library.files.StandardFileInstance;

public class BookInstance extends DomainEntity {

    private Isbn13 isbn13;

    private Book book;

    private BookFormat format;

    private FileInstance file;

    private FileInstance cover;

    private BookInstance(long id) {
        super(id);
    }

    @Deprecated
    public BookInstance(long id, Book book, Isbn13 isbn13, BookFormat format, FileInstance file) {
        super(id);
        this.book = book;
        this.isbn13 = isbn13;
        this.format = format;
        this.file = file;
        this.cover = StandardFileInstance.EMPTY;
    }

    @Deprecated
    public BookInstance(long id, Book book, Isbn13 isbn13, BookFormat format) {
        super(id);
        this.book = book;
        this.isbn13 = isbn13;
        this.format = format;
        this.file = this.cover = StandardFileInstance.EMPTY;
    }

    public Book getBook() {
        return book;
    }

    public void setFile(FileInstance file) {
        this.file = file;
    }

    public void setCover(FileInstance cover) {
        this.cover = cover;
    }

    public FileInstance getCover() {
        return cover;
    }

    public FileInstance getFile() {
        return file;
    }

    public Isbn13 getIsbn13() {
        return isbn13;
    }

    public void setFormat(BookFormat format) {
        this.format = format;
    }

    public BookFormat getFormat() {
        return format;
    }


    public static final class BookInstanceBuilder {

        private long id;

        private Isbn13 isbn13;

        private Book book;

        private BookFormat format;

        private FileInstance file;

        private FileInstance cover;

        private BookInstanceBuilder() {
        }

        public static BookInstanceBuilder aBookInstance() {
            return new BookInstanceBuilder();
        }

        @Required
        public BookInstanceBuilder withId(long id) {
            this.id = id;
            return this;
        }

        @Required
        public BookInstanceBuilder withIsbn13(Isbn13 isbn13) {
            this.isbn13 = isbn13;
            return this;
        }

        @Required
        public BookInstanceBuilder withBook(Book book) {
            this.book = book;
            return this;
        }

        @Required
        public BookInstanceBuilder withFormat(BookFormat format) {
            this.format = format;
            return this;
        }

        public BookInstanceBuilder withFile(FileInstance file) {
            this.file = file;
            return this;
        }

        public BookInstanceBuilder withCover(FileInstance cover) {
            this.cover = cover;
            return this;
        }

        public BookInstance build() {
            if (id <= 0)
                throw new IllegalStateException("id must be positive long");
            if (isbn13 == null)
                throw new IllegalStateException("isbn13 is required");
            if (format == null)
                throw new IllegalStateException("format is required");
            if (book == null)
                throw new IllegalStateException("book is required");
            if (file == null)
                file = StandardFileInstance.EMPTY;
            if (cover == null)
                cover = StandardFileInstance.EMPTY;
            BookInstance bookInstance = new BookInstance(id);
            bookInstance.isbn13 = isbn13;
            bookInstance.book = book;
            bookInstance.format = format;
            bookInstance.file = file;
            bookInstance.cover = file;

            return bookInstance;
        }
    }
}
