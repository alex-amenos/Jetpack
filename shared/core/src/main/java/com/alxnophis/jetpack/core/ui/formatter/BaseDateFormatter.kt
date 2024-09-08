package com.alxnophis.jetpack.core.ui.formatter

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("unused")
class BaseDateFormatter(
    private val locale: Locale = Locale.getDefault(),
) {
    fun formatToReadableDateTime(date: Date): String = date.formatDateWith(PATTERN_READABLE_DATE_TIME)

    fun formatToDayMonthYearTime(date: Date): String = date.formatDateWith(PATTERN_DAY_MONTH_YEAR_TIME)

    fun formatToReadableDateTimeSnakeCase(date: Date): String = date.formatDateWith(PATTERN_READABLE_DATE_TIME_FILE)

    private fun Date.formatDateWith(pattern: String): String {
        val formatter = SimpleDateFormat(pattern, locale)
        return formatter.format(this)
    }

    companion object {
        private const val PATTERN_READABLE_DATE_TIME = "dd MMM yyyy HH:mm"
        private const val PATTERN_DAY_MONTH_YEAR_TIME = "dd/MM/yy HH:mm"
        private const val PATTERN_READABLE_DATE_TIME_FILE = "dd_MMM_yyyy_HH_mm"
    }
}
