package com.jkojote.library.domain.model.reader;

import com.jkojote.library.domain.model.book.instance.BookInstance;

import java.time.LocalDate;

/**
 * Represents an object that encapsulates information about
 * download of the {@link Reader}
 */
public class Download {

    private Reader reader;

    private BookInstance instance;

    private LocalDate lastDateDownloaded;

    private int readerRating;

    public Download(Reader reader, BookInstance instance, LocalDate dateDownloaded, int readerRating) {
        this.reader = reader;
        this.instance = instance;
        this.lastDateDownloaded = dateDownloaded;
        this.readerRating = readerRating;
    }

    public Reader getReader() {
        return reader;
    }

    public BookInstance getInstance() {
        return instance;
    }

    public LocalDate getLastDateDownloaded() {
        return lastDateDownloaded;
    }

    public void setReaderRating(int readerRating) {
        this.readerRating = readerRating;
    }

    public int getReaderRating() {
        return readerRating;
    }
}