package com.jkojote.library.domain.model.book;

import com.jkojote.library.domain.model.book.events.BookInstanceAddedEvent;
import com.jkojote.library.domain.model.book.events.BookInstanceRemovedEvent;
import com.jkojote.library.domain.model.book.instance.BookInstance;
import com.jkojote.library.domain.model.publisher.Publisher;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.neovisionaries.i18n.LanguageCode;

import java.util.Collections;
import java.util.List;

public class Book extends DomainEntity {

    private Work basedOn;

    private Publisher publisher;

    private int edition;

    private String title;

    private LanguageCode language;

    private List<BookInstance> bookInstances;

    public Book(long id, Work basedOn,
                Publisher publisher, int edition,
                List<BookInstance> instances) {
        super(id);
        this.basedOn   = basedOn;
        this.publisher = publisher;
        this.edition   = edition;
        this.bookInstances = instances;
        this.language = LanguageCode.undefined;
    }

    public Book(long id, Work basedOn, List<BookInstance> bookInstances) {
        super(id);
        this.basedOn = basedOn;
        this.bookInstances = bookInstances;
        this.language = LanguageCode.undefined;
    }

    public Work getBasedOn() {
        return basedOn;
    }

    public int getEdition() {
        return edition;
    }

    public void setEdition(int edition) {
        this.edition = edition;
    }

    public void setLanguage(LanguageCode language) {
        this.language = language;
    }

    public LanguageCode getLanguage() {
        return language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public List<BookInstance> getBookInstances() {
        return Collections.unmodifiableList(bookInstances);
    }

    public boolean addBookInstance(BookInstance instance) {
        if (!instance.getBook().equals(this))
            return false;
        if (!bookInstances.add(instance)) {
            return false;
        }
        notifyAllListeners(new BookInstanceAddedEvent(this, instance, null));
        return true;
    }

    public boolean removeBookInstance(BookInstance instance) {
        if (!bookInstances.remove(instance))
            return false;
        notifyAllListeners(new BookInstanceRemovedEvent(this, instance, null));
        return true;
    }
}
