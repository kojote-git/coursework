package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainObject;

public interface LazyObject<T> extends DomainObject {

    boolean isFetched();

    T get();
}
