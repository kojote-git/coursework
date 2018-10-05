package com.jkojote.library.domain.shared.domain;

public abstract class ValueObject implements DomainObject {

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(Object obj);
}
