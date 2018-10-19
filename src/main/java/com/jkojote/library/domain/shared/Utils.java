package com.jkojote.library.domain.shared;

import com.google.common.collect.ForwardingList;
import com.jkojote.library.domain.model.author.Author;
import com.jkojote.library.domain.model.work.Work;
import com.jkojote.library.domain.shared.domain.DomainObject;
import com.jkojote.library.domain.shared.domain.DomainList;
import com.jkojote.library.values.DateRangePrecision;
import com.jkojote.library.persistence.LazyList;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.*;

public final class Utils {

    private static final SqlParameterSource EMPTY = new EmptySqlParameterSource();

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

    public static SqlParameterSource emptyParams() {
        return EMPTY;
    }

    public static SqlParameterSource paramsForAuthor(Author author) {
        return new MapSqlParameterSource("id", author.getId())
                .addValue("firstName", author.getName().getFirstName())
                .addValue("middleName", author.getName().getMiddleName())
                .addValue("lastName", author.getName().getLastName());
    }

    public static SqlParameterSource paramsForWork(Work work) {
        return new MapSqlParameterSource("id", work.getId())
                .addValue("title", work.getTitle());
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

        @Override
        public List<T> get() {
            if (list == null)  {
                list = Collections.unmodifiableList(source);
            }
            return list;
        }
    }
}
