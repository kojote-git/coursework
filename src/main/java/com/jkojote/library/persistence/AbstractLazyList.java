package com.jkojote.library.persistence;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.shared.DomainEntity;
import com.jkojote.library.domain.shared.DomainObject;

import java.util.List;

public abstract class AbstractLazyList<ParentEntity extends DomainEntity, Child extends DomainObject>
extends ForwardingList<Child> implements LazyList<Child> {

    private ParentEntity entity;

    private List<Child> list;

    private ListFetcher<ParentEntity, Child> fetcher;

    private boolean canSetParentEntity;

    public AbstractLazyList(ParentEntity entity, ListFetcher<ParentEntity, Child> fetcher) {
        this.entity = entity;
        this.fetcher = fetcher;
        canSetParentEntity = false;
    }

    public AbstractLazyList(ListFetcher<ParentEntity, Child> fetcher) {
        this.fetcher = fetcher;
        canSetParentEntity = true;
    }

    @Override
    public boolean isFetched() {
        return list != null;
    }

    @Override
    protected List<Child> delegate() {
        if (list == null) {
            list = fetcher.fetchFor(entity);
        }
        return list;
    }

    public ParentEntity getEntity() {
        return entity;
    }

    public void setParentEntity(ParentEntity entity) {
        if (canSetParentEntity) {
            this.entity = entity;
        }
        throw new ParentEntityCannotBeSet("cannot perform this " +
                "action because object is sealed for external modifications");
    }

    public void seal() {
        canSetParentEntity = false;
    }

    public static class ParentEntityCannotBeSet extends RuntimeException {
        public ParentEntityCannotBeSet() {
        }

        public ParentEntityCannotBeSet(String message) {
            super(message);
        }
    }
}
