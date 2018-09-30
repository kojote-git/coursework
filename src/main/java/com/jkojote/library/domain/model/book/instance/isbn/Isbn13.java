package com.jkojote.library.domain.model.book.instance.isbn;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Isbn13 {

    private String number;

    private Isbn13(String number) {
        this.number = number;
    }

    public static Isbn13 of(String number) {
        checkNotNull(number);
        return new Isbn13(number);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Isbn13) {
            Isbn13 that = (Isbn13) obj;
            return number.equals(that.number);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }
}
