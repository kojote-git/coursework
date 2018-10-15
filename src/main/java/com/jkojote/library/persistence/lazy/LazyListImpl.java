package com.jkojote.library.persistence.lazy;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.shared.domain.DomainEntity;
import com.jkojote.library.domain.shared.domain.DomainObject;
import com.jkojote.library.persistence.LazyList;
import com.jkojote.library.persistence.ListFetcher;
import com.jkojote.library.persistence.Refreshable;

import java.util.List;

public class LazyListImpl<ParentEntity extends DomainEntity, Child extends DomainObject>
extends ForwardingList<Child> implements LazyList<Child>, Refreshable<List<Child>> {

    private ParentEntity parent;

    private List<Child> list;

    private ListFetcher<ParentEntity, Child> fetcher;

    private boolean canSetParentEntity;

    public LazyListImpl(ParentEntity entity, ListFetcher<ParentEntity, Child> fetcher) {
        this.parent = entity;
        this.fetcher = fetcher;
        canSetParentEntity = false;
    }

    public LazyListImpl(ListFetcher<ParentEntity, Child> fetcher) {
        this.fetcher = fetcher;
        canSetParentEntity = true;
    }

    @Override
    public boolean isFetched() {
        return list != null;
    }

    @Override
    protected List<Child> delegate() {
        return get();
    }

    public ParentEntity getEntity() {
        return parent;
    }

    public void setParentEntity(ParentEntity entity) {
        if (canSetParentEntity) {
            this.parent = entity;
        } else {
            throw new ParentEntityCannotBeSet("cannot perform this " +
                    "action because object is sealed for external modifications");
        }
    }

    public void seal() {
        canSetParentEntity = false;
    }

    @Override
    public List<Child> get() {
        if (list == null) {
            list = fetcher.fetchFor(parent);
        }
        return list;
    }

    @Override
    public void refresh() {
        list = fetcher.fetchFor(parent);
    }

    public static class ParentEntityCannotBeSet extends RuntimeException {
        public ParentEntityCannotBeSet() {
        }

        ParentEntityCannotBeSet(String message) {
            super(message);
        }
    }
}
