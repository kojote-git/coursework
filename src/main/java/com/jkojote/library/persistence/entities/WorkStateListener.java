package com.jkojote.library.persistence.entities;

import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.events.SubjectAddedEvent;
import com.jkojote.library.domain.model.work.events.SubjectRemovedEvent;
import com.jkojote.library.domain.shared.domain.DomainEvent;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WorkStateListener implements DomainEventListener {

    private BridgeTableProcessor<Work, Subject> bridgeTableProcessor;

    @Override
    public void perform(DomainEvent domainEvent) {
        if (domainEvent instanceof SubjectRemovedEvent)
            onSubjectRemoved((SubjectRemovedEvent) domainEvent);
        if (domainEvent instanceof SubjectAddedEvent)
            onSubjectAdded((SubjectAddedEvent) domainEvent);
    }

    private void onSubjectRemoved(SubjectRemovedEvent e) {
        var work = e.getTarget();
        var subject = e.getSubject();
        bridgeTableProcessor.removeRecord(work, subject);
    }

    private void onSubjectAdded(SubjectAddedEvent e) {
        var work = e.getTarget();
        var subject = e.getSubject();
        bridgeTableProcessor.addRecord(work, subject);
    }

}
