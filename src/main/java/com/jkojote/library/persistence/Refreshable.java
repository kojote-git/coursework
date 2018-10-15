package com.jkojote.library.persistence;

/**
 * Lazy object that is able to be refreshed after {@code refresh()} method called.
 * Refreshing means refreshing the state of the instance from the database,
 * overwriting changes made to the lazy object, if any.
 */
public interface Refreshable<T> extends LazyObject<T> {
    void refresh();
}
