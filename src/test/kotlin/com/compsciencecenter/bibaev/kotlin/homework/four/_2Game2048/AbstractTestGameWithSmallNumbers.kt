package com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048

import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.GameBoard
import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.createGameBoard

open class TestGameWithSmallNumbers {
    val width = 4

    fun String.toValues(): List<List<Int>> = trim().split(' ').map { row -> row.map { ch -> (ch - '0').toInt() } }

    fun valuesToString(getElement: (Int, Int) -> Int?) = buildString {
        for (i in 1..width) {
            for (j in 1..width) {
                append(getElement(i, j) ?: 0)
            }
            append(' ')
        }
    }.trim()

    fun createBoard(input: String): GameBoard<Int?> {
        val data = input.toValues()
        val board = createGameBoard<Int?>(width)
        for ((i, j) in board.indices) {
            val ch = data[i - 1][j - 1]
            if (ch != 0) {
                board[i, j] = ch
            }
        }
        return board
    }

    fun GameBoard<Int?>.print() = valuesToString { i, j -> this[i, j] }
}