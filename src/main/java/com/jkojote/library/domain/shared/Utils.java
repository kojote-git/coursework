package com.jkojote.library.domain.shared;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.shared.domain.DomainObject;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.domain.shared.values.DateRangePrecision;
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

    public static DateRangePrecision convertIntToDateRangePrecision(int code) {
        switch (code) {
            case 0:
                return DateRangePrecision.FULL_RANGE;
            case 1:
                return DateRangePrecision.TO_YEAR;
            case 2:
                return DateRangePrecision.TO_MONTH;
            case 3:
                return DateRangePrecision.EXACT_DATE;
        }
        return null;
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
