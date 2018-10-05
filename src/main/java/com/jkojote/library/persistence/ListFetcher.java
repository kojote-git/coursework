package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.DomainObject;

import java.util.List;

/**
 * Interface for fetching children entities related to parent entity
 * @param <ChildEntity>
 * @param <ParentEntity>
 */
public interface ListFetcher<ParentEntity extends DomainEntity, ChildEntity extends DomainObject>  {

    List<ChildEntity> fetchFor(ParentEntity entity);
}
