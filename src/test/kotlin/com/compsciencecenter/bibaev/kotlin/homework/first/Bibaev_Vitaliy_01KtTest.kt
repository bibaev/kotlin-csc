package com.compsciencecenter.bibaev.kotlin.homework.first

import org.junit.Assert.*
import org.junit.Test
import java.util.*
import java.util.stream.Stream

class Bibaev_Vitaliy_01KtTest {
    @Test(timeout = 10000)
    fun generateSecret() {
        Stream.generate { generateSecret(true) }.limit(1000).allMatch { s -> HashSet(s.toList()).size == 4 }
        Stream.generate { generateSecret(false) }.anyMatch { s -> HashSet(s.toList()).size < 4 }
    }

    @Test
    fun score() {
        assertEquals("Description example failed!", Score(1, 1), score("ACEB", "BCDF"))
        assertEquals("Description example failed!", Score(1, 1), score("AAAF", "ABCA"))
        assertEquals("Full match failed!", Score(4, 0), score("ABCA", "ABCA"))
    }
}