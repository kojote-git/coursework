package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.DomainObject;

import java.util.List;

/**
 * In few words, it represents a list whose contents are dynamically loaded from any data source
 * after invoking any method defined in {@link List} interface.
 * To be more precise, some entity mat have a list of {@code DomainObject}s
 * as one of its attributes and it may be desired not to fetch all these objects
 * when the entity is loaded but delay this fetching until the moment when it is needed.
 * Thus, {@code LazyList} represents an idea of lazy loading
 * @param <T>
 */
public interface LazyList<T extends DomainObject>
extends DomainList<T> {

    /**
     * Whether contents of this list has been fetched from data source
     * @return {@code true} if list has been fetched from data source;
     *         {@code false} otherwise
     */
    boolean isFetched();
}
