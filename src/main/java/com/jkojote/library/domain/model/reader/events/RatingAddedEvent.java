package com.jkojote.library.domain.model.reader.events;

import com.jkojote.library.domain.model.reader.Rating;
import com.jkojote.library.domain.model.reader.Reader;
import com.jkojote.library.domain.shared.domain.DomainEvent;

public class RatingAddedEvent extends DomainEvent<Reader> {

    private Rating rating;

    public RatingAddedEvent(Reader target, Rating rating, String message) {
        super(target, message);
        this.rating = rating;
    }

    public Rating getRating() {
        return rating;
    }
}
