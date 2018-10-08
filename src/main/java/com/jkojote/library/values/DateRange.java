package com.jkojote.library.values;

import com.jkojote.library.domain.shared.Utils;

import java.time.LocalDate;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

//TODO : add functionality
public final class DateRange extends ValueObject {

    private static final DateRange UNKNOWN = new DateRange(null, null, Utils.convertIntToDateRangePrecision(0));

    private LocalDate begins;

    private LocalDate ends;

    private DateRangePrecision precision;

    private DateRange(LocalDate begins, LocalDate ends, DateRangePrecision precision) {
        this.begins = begins;
        this.ends   = ends;
        this.precision = precision;
    }

    public static DateRange of(LocalDate begins, LocalDate ends, DateRangePrecision precision) {
        if (begins == null && ends == null)
            return UNKNOWN;
        checkNotNull(precision);
        if (begins == null) {
            return new DateRange(begins, ends, precision);
        }
        if (ends == null) {
            return new DateRange(begins, ends, precision);
        }
        checkArgument(begins.compareTo(ends) <= 0, "begins must be less than ends");
        return new DateRange(begins, ends, precision);
    }

    public static DateRange unknown() {
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

    @Override
    public int hashCode() {
        return Objects.hash(begins, ends, precision);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof DateRange) {
            DateRange that = (DateRange) obj;
            boolean precisionEqual = precision == that.precision;
            if (!precisionEqual)
                return false;
            boolean beginsEqual = begins == null ? that.begins == null: begins.equals(that.begins);
            boolean endsEqual = ends == null ? that.ends == null : ends.equals(that.ends);
            return beginsEqual && endsEqual;
        }
        return false;
    }
}
