package com.jkojote.library.domain.shared;

/**
 * Represents a listener that performs certain action when particular event occurs
 */
public interface DomainEventListener {

    /**
     * Action to be performed on occurrence of {@code domainEvent}
     * @param domainEvent event to be handled by the listener
     */
    void perform(DomainEvent domainEvent);
}
