package com.jkojote.library.domain.model.work.events;

import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.DomainEvent;

public class WorkFinishedEvent extends DomainEvent<Work> {
    /**
     * @param target  object which has triggered the event
     * @param message optional parameter that describes the event
     */
    public WorkFinishedEvent(Work target, String message) {
        super(target, message);
    }
}
