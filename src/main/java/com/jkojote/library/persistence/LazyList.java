package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.DomainEntity;
import com.jkojote.library.domain.shared.EntityList;

import java.util.List;

/**
 * In few words, it represents a list whose contents are dynamically loaded from any data source
 * after invoking any method defined in {@link List} interface.
 * To be more precise, {@code ParentEntity} is an entity that has a list of {@code ChildEntity}
 * as one of its attributes and it may be desired not to fetch all these entities
 * when ParentEntity is loaded but delay this fetching until the moment when it is needed.
 * Thus, {@code LazyList} represents an idea of lazy loading
 * @param <ChildEntity>
 * @param <ParentEntity>
 */
public interface LazyList<ParentEntity extends DomainEntity, ChildEntity extends DomainEntity>
extends EntityList<ChildEntity> {

    ParentEntity getEntity();
}
