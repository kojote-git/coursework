package com.jkojote.library.persistence.listeners;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Subject;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.events.AuthorAddedEvent;
import com.jkojote.library.domain.model.work.events.AuthorRemovedEvent;
import com.jkojote.library.domain.model.work.events.SubjectAddedEvent;
import com.jkojote.library.domain.model.work.events.SubjectRemovedEvent;
import com.jkojote.library.domain.shared.domain.DomainEvent;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.domain.shared.domain.DomainRepository;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WorkStateListener implements DomainEventListener<Work> {

    private BridgeTableProcessor<Work, Subject> bridgeTableProcessor;

    private BridgeTableProcessor<Work, Author> waBridgeTableProcessor;

    private DomainRepository<Author> authorRepository;

    @Autowired
    public WorkStateListener(
            @Qualifier("WorkSubject")
            BridgeTableProcessor<Work, Subject> bridgeTableProcessor,
            @Qualifier("WorkAuthor")
            BridgeTableProcessor<Work, Author> workAuthorBridgeTableProcessor) {
        this.bridgeTableProcessor = bridgeTableProcessor;
        this.waBridgeTableProcessor = workAuthorBridgeTableProcessor;
    }

    @Autowired
    public void setAuthorRepository(DomainRepository<Author> authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public void perform(DomainEvent domainEvent) {
        if (domainEvent instanceof SubjectRemovedEvent)
            onSubjectRemoved((SubjectRemovedEvent) domainEvent);
        if (domainEvent instanceof SubjectAddedEvent)
            onSubjectAdded((SubjectAddedEvent) domainEvent);
        if (domainEvent instanceof AuthorAddedEvent)
            onAuthorAdded((AuthorAddedEvent) domainEvent);
        if (domainEvent instanceof AuthorRemovedEvent)
            onAuthorRemoved((AuthorRemovedEvent) domainEvent);
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

    private void onAuthorAdded(AuthorAddedEvent e) {
        var author = e.getAuthor();
        var work = e.getTarget();
        if (!authorRepository.exists(author))
            authorRepository.save(author);
        else
            waBridgeTableProcessor.addRecord(work, author);
    }

    private void onAuthorRemoved(AuthorRemovedEvent e) {
        var author = e.getAuthor();
        var work = e.getTarget();
        if (!authorRepository.exists(author))
            return;
        waBridgeTableProcessor.removeRecord(work, author);
    }
}
