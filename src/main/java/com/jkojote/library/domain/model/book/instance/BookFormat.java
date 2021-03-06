package com.jkojote.library.domain.model.book.instance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BookFormat {

    private static final Map<String, BookFormat> CACHE = new ConcurrentHashMap<>();

    public static final BookFormat FB2 = new BookFormat("fb2");

    public static final BookFormat PDF = new BookFormat("pdf");

    public static final BookFormat TXT = new BookFormat("txt");

    static {
        CACHE.put("fb2", FB2);
        CACHE.put("pdf", PDF);
        CACHE.put("txt", TXT);
    }

    private String format;

    private BookFormat(String format) {
        this.format = format;
    }

    public static BookFormat of(String format) {
        checkNotNull(format);
        BookFormat t = CACHE.get(format);
        if (t == null) {
            t = new BookFormat(format);
            if (CACHE.size() < 128)
                CACHE.put(format, t);
        }
        return t;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof BookFormat) {
            BookFormat that = (BookFormat) obj;
            return format.equals(that.format);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return format.hashCode();
    }

    public String asString() {
        return format;
    }

    @Override
    public String toString() {
        return format;
    }
}
