package com.jkojote.library.domain.shared;

/**
 * Represents a domain event
 */
public abstract class DomainEvent {

    /**
     * Object which has triggered the event
     */
    private DomainEntity target;

    /**
     * Optional message that describes the event
     */
    private String message;

    /**
     * @param target object which has triggered the event
     * @param message optional parameter that describes the event
     */
    public DomainEvent(DomainEntity target, String message) {
        this.target = target;
        this.message = message;
    }
}
