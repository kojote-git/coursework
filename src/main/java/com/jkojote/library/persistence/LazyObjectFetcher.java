package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainEntity;

public interface LazyObjectFetcher<Parent extends DomainEntity, Child> {

    Child fetchFor(Parent parent);
}
