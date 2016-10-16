package com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048

/*
If the function double("a") returns "aa",
then the function moveAndMergeEqual transforms the input in the following way:
  a, a, b -> aa, b
  b, null, a, a -> b, aa
  a, a, null, a -> aa, a
  a, null, a, a -> aa, a
Examples and tests in TestMoveAndMergeValues.kt
*/

fun <T : Any> List<T?>.moveAndMergeEqual(double: (T) -> T): List<T> {
    fun inner(ix: Int, acc: List<T>, lst: List<T>): List<T> =
    when {
        ix >= lst.size -> acc
        ix == lst.size - 1 -> acc + lst[ix]
        lst[ix + 1] == lst[ix] -> inner(ix + 2, acc + double(lst[ix]), lst)
        else -> inner(ix + 1, acc + lst[ix], lst)
    }

    return inner(0, listOf(), filterNotNull())
}
