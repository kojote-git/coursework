package com.jkojote.library.domain.model.work.events;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEvent;

public class AuthorAddedEvent extends DomainEvent<Work> {

    private Author author;

    public AuthorAddedEvent(Work target, Author author, String message) {
        super(target, message);
        this.author = author;
    }

    public Author getAuthor() {
        return author;
    }
}
