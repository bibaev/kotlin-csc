package com.compsciencecenter.bibaev.kotlin.homework.third._1Dates

import com.compsciencecenter.bibaev.kotlin.homework.third._1Dates.addTimeIntervals
import com.compsciencecenter.bibaev.kotlin.homework.third._1Dates.nextDay

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

class ComplexInterval(val interval: TimeInterval, val count:Int)

operator fun MyDate.plus(interval: TimeInterval): MyDate = this + ComplexInterval(interval, 1)

operator fun MyDate.plus(complexInterval: ComplexInterval): MyDate =
        addTimeIntervals(complexInterval.interval, complexInterval.count)

enum class TimeInterval {
    DAY,
    WEEK,
    YEAR
}

operator fun TimeInterval.times(count: Int): ComplexInterval = ComplexInterval(this, count)

// 2 * WEEK looks semantically correct too
operator fun Int.times(interval: TimeInterval): ComplexInterval = ComplexInterval(interval, this)
