package com.jkojote.library.domain.shared.values;

public enum DateRangePrecision {

    FULL_RANGE(0),

    TO_YEAR(1),

    TO_MONTH(2),

    EXACT_DATE(3);

    int code;

    DateRangePrecision(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
