package com.compsciencecenter.bibaev.kotlin.homework.four._3GameOfFifteen

import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.*
import com.compsciencecenter.bibaev.kotlin.homework.four._1Game.Game
import java.util.*

fun newGameOfFifteen(): Game = GameOfFifteen()

class GameOfFifteen : Game {
    private val board = createGameBoard<Int>(4)

    override fun initialize() {
        val numbers = (1..15).toMutableList()
        numbers.shuffle()
        while (!countParity(numbers)) {
            numbers.shuffle()
        }

        numbers.zip(board.getAllCells()).forEach { p -> board[p.second] = p.first }
    }

    override fun canMove(): Boolean {
        return true
    }

    override fun hasWon(): Boolean {
        return board.toList().isSorted()
    }

    override fun processMove(direction: Direction) {
        with(board) {
            val emptyCell = getEmptyCell()
            val neighbour = emptyCell.getNeighbour(direction.reversed())
            if(neighbour != null) {
                set(emptyCell, get(neighbour))
                set(neighbour, null)
            }
        }
    }

    override fun get(i: Int, j: Int): Int? = board[i, j]
}

/*
Implement the Game of Fifteen (https://en.wikipedia.org/wiki/15_puzzle).
When you finish, you can play the game by executing 'PlayGameOfFifteen' (or choosing the corresponding run configuration).
 */

// https://github.com/gazolla/Kotlin-Algorithm/blob/master/Shuffle/Shuffle.kt
fun <T : Comparable<T>> MutableList<T>.shuffle(): Unit {
    val random = Random()
    for (i in 0..size - 1) {
        val pos = random.nextInt(size)
        val tmp: T = get(i)

        set(i, get(pos))
        set(pos, tmp)
    }
}

fun <T : Comparable<T>> List<T>.isSorted(): Boolean {
    for (i in 0 until size - 1) {
        if (get(i) > get(i + 1)) {
            return false
        }
    }

    return true
}

fun <T> GameBoard<T>.toList(): List<T> {
    val result = mutableListOf<T>()
    for (cell in getAllCells()) {
        val value = get(cell)
        if (value != null) {
            result.add(value)
        }
    }

    return result
}

fun <T> GameBoard<T>.getEmptyCell(): Cell = getAllCells().first { get(it) == null }
