package com.jkojote.library.domain.shared.values;

import com.jkojote.library.domain.shared.Utils;

import java.time.LocalDate;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

//TODO : add functionality
public final class DateRange {

    private static final DateRange UNKNOWN = new DateRange(null, null, Utils.convertIntToDateRange(0));

    private LocalDate begins;

    private LocalDate ends;

    private DateRangePrecision precision;

    private DateRange(LocalDate begins, LocalDate ends, DateRangePrecision precision) {
        this.begins = begins;
        this.ends   = ends;
        this.precision = precision;
    }

    public static DateRange of(LocalDate begins, LocalDate ends, DateRangePrecision precision) {
        checkNotNull(begins);
        checkNotNull(ends);
        checkNotNull(precision);
        checkArgument(begins.compareTo(ends) <= 0, "begins must be less than ends");
        return new DateRange(begins, ends, precision);
    }

    public static final DateRange unknown() {
        return UNKNOWN;
    }

    public LocalDate getBegins() {
        return begins;
    }

    public LocalDate getEnds() {
        return ends;
    }

    public DateRangePrecision getPrecision() {
        return precision;
    }
}
