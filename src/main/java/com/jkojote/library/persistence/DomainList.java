package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.DomainObject;

import java.util.List;

public interface DomainList<T extends DomainObject> extends List<T> {

}
