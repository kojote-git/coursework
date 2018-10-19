package com.jkojote.library.persistence.listeners;

import com.jkojote.library.domain.model.book.Book;
import com.jkojote.library.domain.model.book.events.BookInstanceAddedEvent;
import com.jkojote.library.domain.model.book.events.BookInstanceRemovedEvent;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.shared.domain.DomainEvent;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("bookStateListener")
@Transactional
class BookStateListener implements DomainEventListener<Book> {

    private TableProcessor<BookInstance> bookInstanceTable;

    @Autowired
    public BookStateListener(@Qualifier("bookInstanceTable")
                             TableProcessor<BookInstance> tableProcessor) {
        this.bookInstanceTable = tableProcessor;
    }

    @Override
    public void perform(DomainEvent domainEvent) {
        if (domainEvent instanceof BookInstanceAddedEvent)
            onBookInstanceAdded((BookInstanceAddedEvent) domainEvent);
        if (domainEvent instanceof BookInstanceRemovedEvent)
            onBookInstanceRemoved((BookInstanceRemovedEvent) domainEvent);
    }

    private void onBookInstanceAdded(BookInstanceAddedEvent e) {
        BookInstance bookInstance = e.getBookInstance();
        bookInstanceTable.insert(bookInstance);
    }

    private void onBookInstanceRemoved(BookInstanceRemovedEvent e) {
        BookInstance bookInstance = e.getBookInstance();
        bookInstanceTable.delete(bookInstance);
    }
}
