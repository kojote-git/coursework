package com.jkojote.library.persistence;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.shared.DomainEntity;

import java.util.List;

public abstract class AbstractLazyList<ParentEntity extends DomainEntity, ChildEntity extends DomainEntity >
extends ForwardingList<ChildEntity> implements LazyList<ParentEntity, ChildEntity> {

    private ParentEntity entity;

    private List<ChildEntity> list;

    private ListFetcher<ParentEntity, ChildEntity> fetcher;

    protected AbstractLazyList(ParentEntity entity, ListFetcher<ParentEntity, ChildEntity> fetcher) {
        this.entity = entity;
        this.fetcher = fetcher;
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
}
