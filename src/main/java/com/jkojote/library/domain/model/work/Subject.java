package com.jkojote.library.domain.model.work;

import com.jkojote.library.values.ValueObject;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Subject extends ValueObject {

    private static final Map<String, Subject> CACHE = new HashMap<>();

    private String subject;

    private Subject(String value) {
        this.subject = value;
    }

    public static Subject of(String subject) {
        checkNotNull(subject);
        if (subject.length() > 32) {
            throw new IllegalArgumentException("length must be less than 32");
        }
        Subject cached = CACHE.get(subject);
        if (cached != null)
            return cached;
        if (CACHE.size() < 128) {
            Subject res = new Subject(subject);
            CACHE.put(subject, res);
            return res;
        }
        return new Subject(subject);
    }

    @Override
    public String toString() {
        return subject;
    }

    public String asString() {
        return subject;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Subject) {
            Subject that = (Subject) obj;
            return subject.equals(that.subject);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return subject.hashCode();
    }
}
