package com.jkojote.library.domain.shared.domain;

/**
 * Represents a listener that performs certain action when particular event occurs
 */
public interface DomainEventListener<T extends DomainEntity> {

    /**
     * Action to be performed on occurrence of {@code domainEvent}
     * @param domainEvent event to be handled by the listener
     */
    void perform(DomainEvent domainEvent);
}
