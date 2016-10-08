package com.compsciencecenter.bibaev.kotlin.homework.third._2Board

open class SquareBoardImpl(override val width: Int) : SquareBoard {
    override fun getCell(i: Int, j: Int): Cell {
        if (checkBounds(i)) {
            if (checkBounds(j)) {
                return CellImpl(i, j)
            }
            throw IllegalArgumentException("Index \"j\" should be from 1 to $width inclusive")
        }

        throw IllegalArgumentException("Index \'i\' should be from 1 fo $width inclusive")
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        if (checkBounds(i) && checkBounds(j)) {
            return CellImpl(i, j)
        }

        return null
    }

    override fun getAllCells(): Collection<Cell> {
        val result = mutableListOf<Cell>()
        for (i in 1..width) {
            for (j in 1..width) {
                result.add(CellImpl(i, j))
            }
        }

        return result
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> = jRange.map { j -> CellImpl(i, j) }.toList()

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> = iRange.map { i -> CellImpl(i, j) }.toList()


    override fun Cell.getNeighbour(direction: Direction): Cell? = when (direction) {
        Direction.UP -> getCellOrNull(i - 1, j)
        Direction.DOWN -> getCellOrNull(i + 1, j)
        Direction.RIGHT -> getCellOrNull(i, j + 1)
        Direction.LEFT -> getCellOrNull(i, j - 1)
    }

    private fun checkBounds(index: Int): Boolean = 1 <= index && index <= width

    protected data class CellImpl(override val i: Int, override val j: Int) : Cell
}

class GameBoardImpl<T>(override val width: Int) : SquareBoardImpl(width), GameBoard<T> {
    private val map: MutableMap<Cell, T> = mutableMapOf()
    override fun get(cell: Cell): T? = map[cell]

    override fun set(cell: Cell, value: T?) {
        if (value == null) {
            map.remove(cell)
        } else {
            map[cell] = value
        }
    }

    override fun get(i: Int, j: Int): T? = get(CellImpl(i, j))

    override fun set(i: Int, j: Int, value: T?) = set(CellImpl(i, j), value)

    override fun contains(value: T): Boolean = map.values.contains(value)

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> =
            getAllCells().filter { cell -> predicate(get(cell)) }

    override fun any(predicate: (T?) -> Boolean): Boolean = getAllCells().any { cell -> predicate(get(cell)) }

    override fun all(predicate: (T?) -> Boolean): Boolean = getAllCells().all { cell -> predicate(get(cell)) }
}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)

fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl(width)
