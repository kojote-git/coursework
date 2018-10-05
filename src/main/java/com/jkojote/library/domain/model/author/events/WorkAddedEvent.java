package com.jkojote.library.domain.model.author.events;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainEvent;

public class WorkAddedEvent extends DomainEvent<Author> {

    private Work work;

    /**
     * @param target  object which has triggered the event
     * @param message optional parameter that describes the event
     */
    public WorkAddedEvent(Author target, Work work, String message) {
        super(target, message);
        this.work = work;
    }

    public Work getWork() {
        return work;
    }
}
