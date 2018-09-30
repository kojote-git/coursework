package com.jkojote.library.domain.model.book.events;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.DomainEvent;

public class BookInstanceAddedEvent extends DomainEvent<Book> {

    private BookInstance bookInstance;

    /**
     * @param target  object which has triggered the event
     * @param message optional parameter that describes the event
     */
    public BookInstanceAddedEvent(Book target, BookInstance bookInstance, String message) {
        super(target, message);
        this.bookInstance = bookInstance;
    }

    public BookInstance getBookInstance() {
        return bookInstance;
    }
}
