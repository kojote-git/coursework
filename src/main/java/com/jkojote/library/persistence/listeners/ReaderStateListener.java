package com.jkojote.library.persistence.listeners;

import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Rating;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.model.reader.events.DownloadAddedEvent;
import com.jkojote.library.domain.model.reader.events.DownloadRemovedEvent;
import com.jkojote.library.domain.model.reader.events.RatingAddedEvent;
import com.jkojote.library.domain.model.reader.events.RatingUpdatedEvent;
import com.jkojote.library.domain.shared.domain.DomainEvent;
import com.jkojote.library.domain.shared.domain.DomainEventListener;
import com.jkojote.library.persistence.TableProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("readerStateListener")
@Transactional
class ReaderStateListener implements DomainEventListener<Reader> {

    private TableProcessor<Download> downloadTable;

    private TableProcessor<Rating> ratingTable;

    public ReaderStateListener(
            @Qualifier("downloadTable")
            TableProcessor<Download> downloadTable,
            @Qualifier("ratingTable")
            TableProcessor<Rating> ratingTable) {
        this.downloadTable = downloadTable;
        this.ratingTable = ratingTable;
    }

    @Override
    public void perform(DomainEvent domainEvent) {
        if (domainEvent instanceof DownloadAddedEvent)
            onDownloadAddedEvent((DownloadAddedEvent) domainEvent);
        if (domainEvent instanceof RatingUpdatedEvent)
            onRatingUpdatedEvent((RatingUpdatedEvent) domainEvent);
        if (domainEvent instanceof RatingAddedEvent)
            onRatingAddedEvent((RatingAddedEvent) domainEvent);
        if (domainEvent instanceof DownloadRemovedEvent)
            onDownloadRemovedEvent((DownloadRemovedEvent) domainEvent);
    }

    private void onDownloadAddedEvent(DownloadAddedEvent e) {
        downloadTable.insert(e.getDownload());
    }

    private void onDownloadRemovedEvent(DownloadRemovedEvent e) {
        downloadTable.delete(e.getDownload());
    }

    private void onRatingUpdatedEvent(RatingUpdatedEvent e) {
        ratingTable.update(e.getRating());
    }

    private void onRatingAddedEvent(RatingAddedEvent e) {
        ratingTable.insert(e.getRating());
    }
}
