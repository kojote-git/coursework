package com.jkojote.library.domain.model.reader.events;

import com.jkojote.library.domain.model.reader.Download;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainEvent;

public class DownloadAddedEvent extends DomainEvent<Reader> {

    private Download download;

    public DownloadAddedEvent(Reader target, Download download, String message) {
        super(target, message);
        this.download = download;
    }

    public Download getDownload() {
        return download;
    }
}
