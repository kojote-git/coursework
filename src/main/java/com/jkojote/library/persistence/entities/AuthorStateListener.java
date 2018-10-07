package com.jkojote.library.persistence.entities;

import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.author.events.WorkAddedEvent;
import com.jkojote.library.domain.model.author.events.WorkRemovedEvent;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.model.work.WorkRepository;
import com.jkojote.library.domain.shared.domain.DomainEvent;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.BridgeTableProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class AuthorStateListener implements DomainEventListener {

    private NamedParameterJdbcTemplate namedJdbcTemplate;

    private WorkRepository workRepository;

    private BridgeTableProcessor<Work, Author> bridgeTableProcessor;

    @Autowired
    public AuthorStateListener(NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    @Autowired
    public void setBridgeTableProcessor(BridgeTableProcessor<Work, Author> bridgeTableProcessor) {
        this.bridgeTableProcessor = bridgeTableProcessor;
    }

    @Autowired
    public void setWorkRepository(WorkRepository workRepository) {
        this.workRepository = workRepository;
    }

    @Override
    public void perform(DomainEvent domainEvent) {
        if (domainEvent instanceof WorkAddedEvent)
            onWorkAdded((WorkAddedEvent) domainEvent);
        if (domainEvent instanceof WorkRemovedEvent)
            onWorkRemoved((WorkRemovedEvent) domainEvent);
    }

    private void onWorkAdded(WorkAddedEvent e) {
        var author = e.getTarget();
        var work = e.getWork();
        if (!workRepository.exists(work))
            workRepository.save(work);
        else
            bridgeTableProcessor.addRecord(work, author);
    }

    private void onWorkRemoved(WorkRemovedEvent e) {
        var author = e.getTarget();
        var work = e.getWork();
        if (!workRepository.exists(work))
            return;
        bridgeTableProcessor.removeRecord(work, author);
    }
}
