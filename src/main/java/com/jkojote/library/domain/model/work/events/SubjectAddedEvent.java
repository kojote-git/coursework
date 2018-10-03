package com.jkojote.library.domain.model.work.events;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.DomainEvent;

public class SubjectAddedEvent extends DomainEvent<Work> {

    private Subject subject;

    public SubjectAddedEvent(Work target, Subject subject, String message) {
        super(target, message);
        this.subject = subject;
    }

    public Subject getSubject() {
        return subject;
    }
}
