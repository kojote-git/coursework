package com.jkojote.library.persistence;

import com.jkojote.library.domain.shared.domain.DomainObject;

import java.util.Collection;

public interface TableProcessor<Entity extends DomainObject> {

    boolean exists(Entity e);

    boolean insert(Entity e);

    boolean delete(Entity e);

    boolean update(Entity e);

    default void batchInsert(Collection<Entity> c) {
        for (Entity e : c)
            insert(e);
    }

    default void batchDelete(Collection<Entity> c) {
        for (Entity e : c)
            delete(e);
    }

    default void batchUpdate(Collection<Entity> c) {
        for (Entity e : c)
            update(e);
    }
}
