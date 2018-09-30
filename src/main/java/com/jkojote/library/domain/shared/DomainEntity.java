package com.jkojote.library.domain.shared;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract entity with the capabilities of event sourcing.
 * Every domain object derives from this class
 */
public abstract class DomainEntity {

    private long id;

    private List<DomainEventListener> eventListeners;

    protected DomainEntity(long id) {
        this.id = id;
        this.eventListeners = new ArrayList<>();
    }

    /**
     * Notifies every event listener from the list that certain event has occurred
     * @param event event that has occurred
     */
    protected void notifyAllListeners(DomainEvent event) {
        for (var listener: eventListeners) {
            listener.perform(event);
        }
    }

    /**
     * Adds {@code listener} to list of event listener
     * @param listener listener to be added to list of other event listeners
     * @return {@code true} if listener has been added
     */
    public boolean addEventListener(DomainEventListener listener) {
        return eventListeners.add(listener);
    }

    public boolean removeListener(DomainEventListener listener) {
        return eventListeners.remove(listener);
    }

    public long getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof DomainEntity) {
            DomainEntity that = (DomainEntity) obj;
            return id == that.id;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}
