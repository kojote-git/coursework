package com.jkojote.library.domain.shared;

import java.util.List;

public interface EntityList<T extends DomainEntity> extends List<T> {

    /**
     * Whether contents of this list has been fetched from data source
     * @return {@code true} if list has been fetched from data source;
     *         {@code false} otherwise
     */
    boolean isFetched();

}
