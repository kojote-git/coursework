package com.jkojote.library.clauses;

public enum SortOrder {
    ASC("ASC"), DESC("DESC");

    private String str;

    SortOrder(String str) {
        this.str = str;
    }

    public String asString() {
        return str;
    }
}

