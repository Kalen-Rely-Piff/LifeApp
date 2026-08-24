package com.lifeapp.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Calendar

class DateUtilTest {

    @Test
    fun today_returnsValidFormat() {
        val today = DateUtil.today()
        // Should match yyyy-MM-dd pattern
        assert(today.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
    }

    @Test
    fun formatDate_returnsCorrectFormat() {
        val cal = Calendar.getInstance()
        cal.set(2026, 7, 23) // August 23, 2026 (month is 0-indexed)
        val result = DateUtil.formatDate(cal.time)
        assertEquals("2026-08-23", result)
    }

    @Test
    fun parseDate_validDate_returnsDate() {
        val date = DateUtil.parseDate("2026-08-23")
        assertNotNull(date)
        val cal = Calendar.getInstance()
        cal.time = date!!
        assertEquals(2026, cal.get(Calendar.YEAR))
        assertEquals(Calendar.AUGUST, cal.get(Calendar.MONTH))
        assertEquals(23, cal.get(Calendar.DAY_OF_MONTH))
    }

    @Test
    fun parseDate_invalidDate_returnsNull() {
        val date = DateUtil.parseDate("invalid-date")
        assertNull(date)
    }

    @Test
    fun addDays_positiveDays_returnsLaterDate() {
        val result = DateUtil.addDays("2026-08-23", 1)
        assertEquals("2026-08-24", result)
    }

    @Test
    fun addDays_negativeDays_returnsEarlierDate() {
        val result = DateUtil.addDays("2026-08-23", -1)
        assertEquals("2026-08-22", result)
    }

    @Test
    fun addDays_crossMonth_returnsCorrectDate() {
        val result = DateUtil.addDays("2026-08-31", 1)
        assertEquals("2026-09-01", result)
    }

    @Test
    fun addDays_crossYear_returnsCorrectDate() {
        val result = DateUtil.addDays("2026-12-31", 1)
        assertEquals("2027-01-01", result)
    }

    @Test
    fun displayDate_returnsChineseFormat() {
        val result = DateUtil.displayDate("2026-08-23")
        // Should contain month and day
        assert(result.contains("8月") || result.contains("08月"))
        assert(result.contains("23日"))
    }

    @Test
    fun getMonthDays_returnsCorrectDays() {
        assertEquals(31, DateUtil.getMonthDays(2026, 0)) // January
        assertEquals(28, DateUtil.getMonthDays(2026, 1)) // February 2026 (not leap)
        assertEquals(29, DateUtil.getMonthDays(2024, 1)) // February 2024 (leap)
        assertEquals(31, DateUtil.getMonthDays(2026, 7)) // August
        assertEquals(30, DateUtil.getMonthDays(2026, 3)) // April
    }
}
