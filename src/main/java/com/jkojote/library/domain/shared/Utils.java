package com.jkojote.library.domain.shared;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.persistence.DomainList;
import com.jkojote.library.persistence.LazyList;

import java.util.*;

public final class Utils {

    @SuppressWarnings("unchecked")
    public static <T extends DomainObject>
    DomainList<T> unmodifiableDomainList(DomainList<T> entityList) {
        return entityList instanceof LazyList ?
                new UnmodifiableLazyList((LazyList<T>) entityList) :
                new UnmodifiableDomainList<>(entityList);
    }

    private static class UnmodifiableDomainList<T extends DomainObject>
    extends ForwardingList<T> implements DomainList<T> {

        private List<T> list;

        UnmodifiableDomainList(DomainList<T> list) {
            this.list = Collections.unmodifiableList(list);
        }

        @Override
        protected List<T> delegate() {
            return list;
        }
    }

    private static class UnmodifiableLazyList<T extends DomainObject>
    extends ForwardingList<T> implements LazyList<T> {

        private LazyList<T> source;

        private List<T> list;

        UnmodifiableLazyList(LazyList<T> list) {
            this.source = list;
            if (list.isFetched()) {
                this.list = Collections.unmodifiableList(list);
            }
        }

        @Override
        protected List<T> delegate() {
            if (list == null)  {
                list = Collections.unmodifiableList(source);
            }
            return list;
        }

        @Override
        public boolean isFetched() {
            return source.isFetched();
        }
    }
}
