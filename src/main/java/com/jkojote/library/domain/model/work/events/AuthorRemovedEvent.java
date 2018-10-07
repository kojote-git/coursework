package com.jkojote.library.domain.model.work.events;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEvent;

public class AuthorRemovedEvent extends DomainEvent<Work> {

    private Author author;

    public AuthorRemovedEvent(Work target, Author author, String message) {
        super(target, message);
        this.author = author;
    }

    public Author getAuthor() {
        return author;
    }
}
