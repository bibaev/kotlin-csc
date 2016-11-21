package _SurnameName

import bloxorz.Direction
import bridges.BridgesInfo
import bloxorz.Game

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'createGame' below should live in the root _SurnameName package.
// Please DON'T copy the interface 'Game' here.

class GameImpl : Game {
    private data class Position(val i: Int, val j: Int)

    private val myHeight: Int
    private val myWidth: Int

    private val myPositions: Map<Int, MutableMap<Int, Position>>
    private val myPosition2Char: Map<Position, Char>

    private val myTarget: Position
    private val myStart: Position
    private var myFirstBlockPosition: Position
    private var mySecondBlockPosition: Position

    constructor(board: String) {
        val boardArrays = board.split('\n')
                .map { x -> x.trimEnd().toCharArray().filterIndexed { i, x -> i % 2 == 0 } }

        assert(boardArrays.size > 0, { "Board cannot be empty" })

        val positionsMap = mutableMapOf<Int, MutableMap<Int, Position>>()
        val pos2Char = mutableMapOf<Position, Char>()
        boardArrays.indices.forEach { i ->
            run {
                val nestedMap = mutableMapOf<Int, Position>()
                boardArrays[i].indices.forEach { j ->
                    run {
                        val pos = Position(i, j)
                        val ch = boardArrays[i][j]
                        if (ch != ' ') {
                            pos2Char[pos] = boardArrays[i][j]
                            nestedMap[j] = pos
                        }

                    }
                }
                positionsMap[i] = nestedMap
            }
        }

        myPositions = positionsMap
        myPosition2Char = pos2Char

        myHeight = boardArrays.size
        myWidth = boardArrays.map { x -> x.size }.max()!!

        val start = findSymbol('S')
        assert(start.size == 1, { "Error: start position must be exactly one" })

        myStart = start[0]
        myFirstBlockPosition = myStart
        mySecondBlockPosition = myFirstBlockPosition

        val target = findSymbol('T')
        assert(target.size <= 1, { "Error: target position must be not greater then one" })

        if (target.isEmpty()) {
            myTarget = Position(-1, -1)
        } else {
            myTarget = target[0]
        }
    }

    override val height: Int
        get() = myHeight
    override val width: Int
        get() = myWidth

    override fun getCellValue(i: Int, j: Int): Char? {
        val position = myPositions[i - 1]?.get(j - 1) ?: return null

        when (position) {
            myFirstBlockPosition -> return 'x'
            mySecondBlockPosition -> return 'x'
            myStart -> return 'S'
            myTarget -> return 'T'
            else -> return myPosition2Char[position]
        }
    }

    override fun processMove(direction: Direction) {
        if (!suggestMoves().orEmpty().contains(direction)) {
            myFirstBlockPosition = myStart
            mySecondBlockPosition = myStart
            return
        }

        when (direction) {
            Direction.UP -> processMove { up(it)!! }
            Direction.DOWN -> processMove { down(it)!! }
            Direction.RIGHT -> processMove { right(it)!! }
            Direction.LEFT -> processMove { left(it)!! }
        }
    }

    override fun hasWon(): Boolean = myFirstBlockPosition == myTarget && mySecondBlockPosition == myTarget

    override fun toString(): String =
            (0 until myHeight).map { i ->
                (0 until myWidth).map { j ->
                    getCellValue(i + 1, j + 1) ?: " "
                }.joinToString(" ")
            }.joinToString("\n", transform = String::trimEnd)


    override fun suggestMoves(): List<Direction>? {
        val result = mutableListOf<Direction>()

        fun check(dir: Direction, move: (Position) -> Position?): Unit {
            if (isMovePossible(move)) {
                result += dir
            }
        }

        check(Direction.UP, { up(it) })
        check(Direction.DOWN, { down(it) })
        check(Direction.LEFT, { left(it) })
        check(Direction.RIGHT, { right(it) })

        if (result.isEmpty()) {
            return null
        }

        return result
    }

    private fun findSymbol(sym: Char): List<Position> = myPosition2Char.filterValues { x -> x == sym }.keys.toList()

    private fun isMovePossible(move: (Position) -> Position?): Boolean =
            move(myFirstBlockPosition) != null && move(mySecondBlockPosition) != null &&
                    (myFirstBlockPosition != mySecondBlockPosition || move(move(mySecondBlockPosition)!!) != null);


    private fun processMove(move: (Position) -> Position) {
        if (myFirstBlockPosition == mySecondBlockPosition) {
            mySecondBlockPosition = move(mySecondBlockPosition)
        } else if (mySecondBlockPosition == move(myFirstBlockPosition)) {
            myFirstBlockPosition = mySecondBlockPosition
        } else if (myFirstBlockPosition == move(mySecondBlockPosition)) {
            mySecondBlockPosition = myFirstBlockPosition
        }

        myFirstBlockPosition = move(myFirstBlockPosition)
        mySecondBlockPosition = move(mySecondBlockPosition)
    }

    private fun up(pos: Position): Position? = myPositions[pos.i - 1]?.get(pos.j)
    private fun left(pos: Position): Position? = myPositions[pos.i]?.get(pos.j - 1)
    private fun right(pos: Position): Position? = myPositions[pos.i]?.get(pos.j + 1)
    private fun down(pos: Position): Position? = myPositions[pos.i + 1]?.get(pos.j)
}

fun createGame(board: String, bridgesInfo: BridgesInfo? = null): Game {
    return GameImpl(board)
}
