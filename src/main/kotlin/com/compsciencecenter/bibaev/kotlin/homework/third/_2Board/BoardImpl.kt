package com.compsciencecenter.bibaev.kotlin.homework.third._2Board

open class SquareBoardImpl(override val width: Int) : SquareBoard {
    override fun getCell(i: Int, j: Int): Cell {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllCells(): Collection<Cell> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class GameBoardImpl<T>(override val width: Int) : SquareBoardImpl(width), GameBoard<T> {
    override fun get(cell: Cell): T? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(cell: Cell, value: T?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(i: Int, j: Int): T? {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun set(i: Int, j: Int, value: T?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun contains(value: T): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun createSquareBoard(width: Int): SquareBoard = SquareBoardImpl(width)

fun <T> createGameBoard(width: Int): GameBoard<T> = GameBoardImpl<T>(width)