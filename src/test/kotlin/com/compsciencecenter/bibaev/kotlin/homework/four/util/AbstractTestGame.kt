package com.compsciencecenter.bibaev.kotlin.homework.four.util

import com.compsciencecenter.bibaev.kotlin.homework.four._1Game.Game
import org.junit.Before

abstract class TestGame {

    protected lateinit var game: Game
    protected val indices = (1..4).flatMap { i -> (1..4).map { j -> i to j } }
    protected val values: List<Int?>
        get() = indices.map { game[it.first, it.second] }

    abstract fun newGame(): Game

    @Before
    fun setUp() {
        game = newGame()
        game.initialize()
    }
}