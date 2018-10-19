package com.jkojote.library.values;

public final class OrdinaryText extends Text {

    public static final OrdinaryText EMPTY = new OrdinaryText("");

    private final String text;

    private final int length;

    private OrdinaryText(String text) {
        this.text = text;
        this.length = text.length();
    }

    public static OrdinaryText of(String str) {
        if (str == null || str.length() == 0)
            return EMPTY;
        return new OrdinaryText(str);
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public int length() {
        return length;
    }
}
