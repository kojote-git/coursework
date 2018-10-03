package com.jkojote.library.persistence;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.shared.DomainEntity;

import java.util.List;

public abstract class AbstractLazyList<ParentEntity extends DomainEntity, ChildEntity extends DomainEntity >
extends ForwardingList<ChildEntity> implements LazyList<ParentEntity, ChildEntity> {

    private ParentEntity entity;

    private List<ChildEntity> list;

    private ListFetcher<ParentEntity, ChildEntity> fetcher;

    private boolean canSetParentEntity;

    public AbstractLazyList(ParentEntity entity, ListFetcher<ParentEntity, ChildEntity> fetcher) {
        this.entity = entity;
        this.fetcher = fetcher;
        canSetParentEntity = false;
    }

    public AbstractLazyList(ListFetcher<ParentEntity, ChildEntity> fetcher) {
        this.fetcher = fetcher;
        canSetParentEntity = true;
    }

    @Override
    public boolean isFetched() {
        return list != null;
    }

    @Override
    protected List<ChildEntity> delegate() {
        if (list == null) {
            list = fetcher.fetchFor(entity);
        }
        return list;
    }

    @Override
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
