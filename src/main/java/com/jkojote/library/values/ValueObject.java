package com.jkojote.library.values;

import com.jkojote.library.domain.shared.domain.DomainObject;

public abstract class ValueObject implements DomainObject {

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
