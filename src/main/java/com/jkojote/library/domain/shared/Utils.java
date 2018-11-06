package com.jkojote.library.domain.shared;

import com.jkojote.library.values.DateRangePrecision;

import java.util.Collection;
import java.util.function.Predicate;

public final class Utils {

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

    public static <T> int indexOf(Collection<T> c, Predicate<T> p) {
        int idx = 0;
        for (T val : c) {
            if (p.test(val))
                return idx;
            idx++;
        }
        return -1;
    }
}
