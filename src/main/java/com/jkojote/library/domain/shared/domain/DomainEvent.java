package com.jkojote.library.domain.shared.domain;

/**
 * Represents a domain event
 */
public abstract class DomainEvent<T extends DomainEntity> {

    /**
     * Object which has triggered the event
     */
    private T target;

    /**
     * Optional message that describes the event
     */
    private String message;

    /**
     * @param target object which has triggered the event
     * @param message optional parameter that describes the event
     */
    public DomainEvent(T target, String message) {
        this.target = target;
        this.message = message;
    }

    public T getTarget() {
        return target;
    }

    public String getMessage() {
        return message;
    }
}
