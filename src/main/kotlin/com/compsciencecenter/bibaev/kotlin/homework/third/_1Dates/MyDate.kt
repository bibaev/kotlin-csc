package _1Dates

data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate): Int {
        val yearComparison = year.compareTo(other.year)
        if (yearComparison != 0) {
            return yearComparison
        }

        val monthComparison = month.compareTo(other.month)
        if (monthComparison != 0) {
            return monthComparison
        }

        return dayOfMonth.compareTo(other.dayOfMonth)
    }
}

class DateRange(val start: MyDate, val endInclusive: MyDate)

operator fun MyDate.rangeTo(other: MyDate): DateRange = DateRange(this, other)

operator fun DateRange.contains(other: MyDate): Boolean = start <= other && other < endInclusive

operator fun DateRange.iterator(): Iterator<MyDate> = object : Iterator<MyDate> {
    var current = start
    override fun hasNext(): Boolean = current <= endInclusive

    override fun next(): MyDate {
        val value = current
        current = current.nextDay()
        return value
    }
}

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR
}