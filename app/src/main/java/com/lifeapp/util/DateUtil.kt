package com.lifeapp.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply { isLenient = false }
    private val displayFormat = SimpleDateFormat("M月d日 EEEE", Locale.CHINESE)

    fun today(): String = dateFormat.format(Date())

    fun formatDate(date: Date): String = dateFormat.format(date)

    fun parseDate(dateStr: String): Date? {
        return try {
            dateFormat.parse(dateStr)
        } catch (e: Exception) {
            null
        }
    }

    fun displayDate(dateStr: String): String {
        return parseDate(dateStr)?.let { displayFormat.format(it) } ?: dateStr
    }

    fun addDays(dateStr: String, days: Int): String {
        val date = parseDate(dateStr) ?: return dateStr
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(cal.time)
    }

    fun getMonthDays(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    fun getFirstDayOfWeek(year: Int, month: Int): Int {
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        return cal.get(Calendar.DAY_OF_WEEK)
    }
}
