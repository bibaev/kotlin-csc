package com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048

import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.Cell
import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.Direction
import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.GameBoard
import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.createGameBoard
import com.compsciencecenter.bibaev.kotlin.homework.four._1Game.Game
import java.util.*

/*
Your task is to implement the game 2048 https://en.wikipedia.org/wiki/2048_(video_game)
Implement the helper function first (moveAndMergeEqual in SurnameNameGame2048Helper.kt), then extension functions below.

Try to use methods of SquareBoard and GameBoard instead of reimplementing them.
(You may use and add extensions like SquareBoard.indices() as well).

When you finish, you can play the game by executing 'PlayGame2048' (or choosing the corresponding run configuration).
 */
fun newGame2048(): Game = Game2048()

class Game2048 : Game {
    private val board = createGameBoard<Int?>(4)

    override fun initialize() {
        board.addRandomValue()
        board.addRandomValue()
    }

    override fun canMove() = board.any { it == null }

    override fun hasWon() = board.any { it == 2048 }

    override fun processMove(direction: Direction) {
        if (board.moveValues(direction)) {
            board.addRandomValue()
        }
    }

    override fun get(i: Int, j: Int): Int? = board[i, j]
}

val random = Random()
fun generateRandomStartValue() = if (random.nextInt(10) == 9) 4 else 2

/*
Add a random value to a free cell in a board.
The value should be 2 for 90% cases, 4 for the rest of the cases.
Use the generateRandomStartValue function above.
Examples and tests in TestAddRandomValue.
 */
fun GameBoard<Int?>.addRandomValue() {
    val freeCells = indices.filter { ix -> get(ix.first, ix.second) == null }.toList()
    if (freeCells.isEmpty()) {
        throw IllegalStateException("The board already filled")
    }

    val randomIndices = freeCells[random.nextInt(freeCells.size)]
    set(randomIndices.first, randomIndices.second, generateRandomStartValue())
}

/*
Move values in a specified rowOrColumn only.
Use the helper function 'moveAndMergeEqual' (in SurnameNameGame2048Helper.kt).
The values should be moved to the beginning of the row (or column), in the same manner as in the function 'moveAndMergeEqual'.
Examples and tests in TestMoveValuesInRowOrColumn.
 */
fun GameBoard<Int?>.moveValuesInRowOrColumn(rowOrColumn: List<Cell>): Boolean {
    val before = rowOrColumn.map { get(it) }
    val res = before.moveAndMergeEqual { value -> value * value }
    res.zip(rowOrColumn).forEach { pair -> set(pair.second, pair.first) }
    rowOrColumn.subList(res.size, rowOrColumn.size).forEach { set(it, null) }

    val after = rowOrColumn.map { get(it) }
    return before != after
}

/*
Move values by the rules of the 2048 game to the specified direction.
Use the moveValuesInRowOrColumn function above.
Examples and tests in TestMoveValues.
 */
fun GameBoard<Int?>.moveValues(direction: Direction): Boolean {
    TODO()
}