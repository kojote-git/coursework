package com.jkojote.library.domain.model.reader;

import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.domain.DomainObject;

import java.time.LocalDateTime;

/**
 * Represents an object that encapsulates information about
 * download of the {@link Reader}
 */
public class Download implements DomainObject {

    private Reader reader;

    private BookInstance instance;

    private LocalDateTime timeDownloaded;

    private int readerRating;

    public Download(Reader reader, BookInstance instance, LocalDateTime dateDownloaded, int readerRating) {
        this.reader = reader;
        this.instance = instance;
        this.timeDownloaded = dateDownloaded;
        this.readerRating = readerRating;
    }

    public Reader getReader() {
        return reader;
    }

    public BookInstance getInstance() {
        return instance;
    }

    public LocalDateTime getTimeDownloaded() {
        return timeDownloaded;
    }

    public void setReaderRating(int readerRating) {
        this.readerRating = readerRating;
    }

    public int getReaderRating() {
        return readerRating;
    }
}