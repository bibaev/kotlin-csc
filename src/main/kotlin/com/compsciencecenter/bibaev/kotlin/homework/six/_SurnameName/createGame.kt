package _SurnameName

import bloxorz.Direction
import bridges.BridgesInfo
import bloxorz.Game
import java.util.*

// Your solution should live in this folder/package only (rename _SurnameName accordingly.)
// You may add as many subpackages as you need, but the function 'createGame' below should live in the root _SurnameName package.
// Please DON'T copy the interface 'Game' here.

class GameImpl : Game {
    private data class Position(val i: Int, val j: Int)

    companion object {
        val DIRECTIONS: List<Direction> = listOf(Direction.UP, Direction.LEFT, Direction.DOWN, Direction.RIGHT)
    }

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
        if (!isMovePossible(toMove(direction))) {
            myFirstBlockPosition = myStart
            mySecondBlockPosition = myStart
            return
        }

        processMove({ x -> toMove(direction).invoke(x)!! })
    }

    override fun hasWon(): Boolean = myFirstBlockPosition == myTarget && mySecondBlockPosition == myTarget

    override fun toString(): String =
            (0 until myHeight).map { i ->
                (0 until myWidth).map { j ->
                    getCellValue(i + 1, j + 1) ?: " "
                }.joinToString(" ")
            }.joinToString("\n", transform = String::trimEnd)


    override fun suggestMoves(): List<Direction>? {
        val previous = mutableMapOf<Pair<Position, Position>, Pair<Direction, Pair<Position, Position>>>()
        val queue: Queue<Pair<Position, Position>> = LinkedList<Pair<Position, Position>>()
        queue.add(myStart to myStart)
        while (!queue.isEmpty()) {
            val prev = queue.poll()
            val (firstPos, secondPos) = prev
            if (firstPos == secondPos && firstPos == myTarget) {
                break
            }

            for (direction in DIRECTIONS.filter { isMovePossible(toMove(it), firstPos, secondPos) }) {
                val next = getNextPositions({ x -> toMove(direction).invoke(x)!! }, firstPos, secondPos)
                if (!previous.contains(next)) {
                    previous[next] = direction to prev
                    queue.add(next)
                }
            }
        }

        val target = myTarget to myTarget
        if (!previous.contains(myTarget to myTarget)) {
            return null
        }

        var prev: Pair<Position, Position>? = target
        val start = myStart to myStart
        val result = mutableListOf<Direction>()
        while (prev != start) {
            val dir2prev = previous[prev]
            if (dir2prev != null) {
                result += dir2prev.first
            }

            prev = dir2prev?.second
        }

        return result.asReversed()
    }

    private fun findSymbol(sym: Char): List<Position> = myPosition2Char.filterValues { x -> x == sym }.keys.toList()

    private fun isMovePossible(move: (Position) -> Position?,
                               fst: Position = myFirstBlockPosition,
                               snd: Position = mySecondBlockPosition): Boolean =
            move(fst) != null && move(snd) != null && (fst != snd || move(move(snd)!!) != null)


    private fun getNextPositions(move: (Position) -> Position,
                                 firstPosition: Position = myFirstBlockPosition,
                                 secondPosition: Position = mySecondBlockPosition)
            : Pair<Position, Position> {
        var first = firstPosition
        var second = secondPosition
        if (first == second) {
            second = move(second)
        } else if (second == move(first)) {
            first = second
        } else if (first == move(second)) {
            second = first
        }

        return move(first) to move(second)
    }

    private fun processMove(move: (Position) -> Position) {
        val newPosition = getNextPositions(move)
        myFirstBlockPosition = newPosition.first
        mySecondBlockPosition = newPosition.second
    }

    private fun toMove(direction: Direction): (Position) -> Position? {
        when (direction) {
            Direction.UP -> return { up(it) }
            Direction.DOWN -> return { down(it) }
            Direction.RIGHT -> return { right(it) }
            Direction.LEFT -> return { left(it) }
        }
    }

    private fun up(pos: Position): Position? = myPositions[pos.i - 1]?.get(pos.j)
    private fun left(pos: Position): Position? = myPositions[pos.i]?.get(pos.j - 1)
    private fun right(pos: Position): Position? = myPositions[pos.i]?.get(pos.j + 1)
    private fun down(pos: Position): Position? = myPositions[pos.i + 1]?.get(pos.j)
}

fun createGame(board: String, bridgesInfo: BridgesInfo? = null): Game {
    return GameImpl(board)
}
