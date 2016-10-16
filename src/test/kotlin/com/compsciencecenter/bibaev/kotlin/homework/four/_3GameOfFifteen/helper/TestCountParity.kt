package com.compsciencecenter.bibaev.kotlin.homework.four._3GameOfFifteen.helper

import org.junit.Assert
import org.junit.Test
import com.compsciencecenter.bibaev.kotlin.homework.four._3GameOfFifteen.countParity

class TestCountParity {
    fun testPermutation(permutation: List<Int>, parity: Boolean) {
        Assert.assertEquals("This permutation is ${if (parity) "even" else "odd"}: $permutation", parity, countParity(permutation))
    }

    fun testPermutation(shortPermutation: Int, parity: Boolean) {
        val permutation = shortPermutation.toString().map { "$it".toInt() }
        testPermutation(permutation, parity)
    }

    fun testEven(shortPermutation: Int) = testPermutation(shortPermutation, true)
    fun testOdd(shortPermutation: Int) = testPermutation(shortPermutation, false)

    @Test fun testEven0() = testEven(123)
    @Test fun testEven1() = testEven(2134)
    @Test fun testEven2() = testEven(1324)
    @Test fun testEven3() = testEven(3214)
    @Test fun testEven4() = testEven(1243)
    @Test fun testEven5() = testEven(4123)
    @Test fun testEven6() = testEven(2413)
    @Test fun testEven7() = testEven(3142)
    @Test fun testEven8() = testEven(1432)
    @Test fun testEven9() = testEven(4312)
    @Test fun testEven10() = testEven(2341)
    @Test fun testEven11() = testEven(4231)
    @Test fun testEven12() = testEven(3421)

    @Test fun testOdd0() = testOdd(132)
    @Test fun testOdd1() = testOdd(1234)
    @Test fun testOdd2() = testOdd(3124)
    @Test fun testOdd3() = testOdd(2314)
    @Test fun testOdd4() = testOdd(2143)
    @Test fun testOdd5() = testOdd(1423)
    @Test fun testOdd6() = testOdd(4213)
    @Test fun testOdd7() = testOdd(1342)
    @Test fun testOdd8() = testOdd(4132)
    @Test fun testOdd9() = testOdd(3412)
    @Test fun testOdd10() = testOdd(3241)
    @Test fun testOdd11() = testOdd(2431)
    @Test fun testOdd12() = testOdd(4321)

    @Test fun testStart() = testPermutation((1..15).toList(), true)
}