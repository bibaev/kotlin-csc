package com.compsciencecenter.bibaev.kotlin.homework.four._1Game

import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.Direction

interface Game {
    fun initialize()
    fun canMove(): Boolean
    fun hasWon(): Boolean
    fun processMove(direction: Direction)
    operator fun get(i: Int, j: Int): Int?
}
