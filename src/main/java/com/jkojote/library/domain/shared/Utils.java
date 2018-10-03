package com.jkojote.library.domain.shared;

import com.google.common.collect.ForwardingList;

import java.util.*;

public final class Utils {
    public static <T extends DomainEntity>
    EntityList<T> unmodifiableEntityList(EntityList<T> entityList) {
        return new UnmodifiableEntityList(entityList);
    }

    private static class UnmodifiableEntityList<T extends DomainEntity>
    extends ForwardingList<T> implements EntityList<T> {

        private EntityList<T> source;

        private List<T> list;

        UnmodifiableEntityList(EntityList<T> entityList) {
            source = entityList;
            if (entityList.isFetched()) {
                list = Collections.unmodifiableList(entityList);
            }
        }

        @Override
        public boolean isFetched() {
            return source.isFetched();
        }

        @Override
        protected List<T> delegate() {
            if (list == null) {
                list = Collections.unmodifiableList(source);
            }
            return list;
        }
    }
}
