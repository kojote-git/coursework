package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainEntity;

import java.util.List;

/**
 * Interface for fetching children entities related to parent entity
 * @param <Child>
 * @param <ParentEntity>
 */
public interface ListFetcher<ParentEntity extends DomainEntity, Child>
extends LazyObjectFetcher<ParentEntity, List<Child>> {

    List<Child> fetchFor(ParentEntity entity);
}
