package com.jkojote.library.domain.model;

import com.jkojote.library.domain.shared.values.DateRange;
import com.jkojote.library.domain.shared.values.DateRangePrecision;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DateRangeTest {

    @Test
    public void equals() {
        DateRange dr1 = DateRange.unknown();
        DateRange dr2 = DateRange.of(null, null, DateRangePrecision.FULL_RANGE);
        assertEquals(dr1, dr2);

        dr1 = DateRange.of(null, LocalDate.now(), DateRangePrecision.FULL_RANGE);
        dr2 = DateRange.of(LocalDate.now(), null, DateRangePrecision.FULL_RANGE);
        assertNotEquals(dr1, dr2);

        var now = LocalDate.now();
        dr1 = DateRange.of(null, now, DateRangePrecision.FULL_RANGE);
        dr2 = DateRange.of(null, now, DateRangePrecision.FULL_RANGE);
        assertEquals(dr1, dr2);

        dr2 = DateRange.of(null, now, DateRangePrecision.TO_YEAR);
        assertNotEquals(dr1, dr2);
    }
}
