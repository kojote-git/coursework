package com.jkojote.library.values;

import com.jkojote.library.domain.shared.domain.DomainObject;

public abstract class Text implements DomainObject {

    @Override
    public abstract String toString();

    public abstract int length();
}
