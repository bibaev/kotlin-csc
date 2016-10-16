package com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048

import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.Cell
import com.compsciencecenter.bibaev.kotlin.homework.four._0Board.SquareBoard
import com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048.TestGameWithSmallNumbers
import org.junit.Assert
import org.junit.Test
import com.compsciencecenter.bibaev.kotlin.homework.four._2Game2048.moveValuesInRowOrColumn

class TestMoveValuesInRowOrColumn : TestGameWithSmallNumbers() {
    private val defaultInput = """
0204
2000
0000
4000"""

    @Test
    fun testRow() = testMoveInRowOrColumn({ it.getRow(1, 1..4) }, """
2400
2000
0000
4000""")

    @Test
    fun testRowReversed() = testMoveInRowOrColumn({ it.getRow(1, 4 downTo 1) }, """
0024
2000
0000
4000""")


    @Test
    fun testColumn() = testMoveInRowOrColumn({ it.getColumn(1..4, 1) }, """
2204
4000
0000
0000""")

    @Test
    fun testColumnReversed() = testMoveInRowOrColumn({ it.getColumn(4 downTo 1, 1) }, """
0204
0000
2000
4000""")

    @Test
    fun testLastValueZero() = testMoveInRowOrColumn({ it.getRow(2, 1 downTo 4) }, """
0204
2000
0000
4000""", expectedMove = false)

    @Test
    fun testNoMove() = testMoveInRowOrColumn({ it.getRow(1, 1..4) },
            "2424 0000 0000 0000", "2424 0000 0000 0000", expectedMove = false)

    fun String.toOneLineInput() = trim().replace('\n', ' ')
    fun String.toMultiLineInput() = trim().replace(' ', '\n')

    fun testMoveInRowOrColumn(
            getRowOrColumn: (SquareBoard) -> List<Cell>,
            expected: String,
            input: String = defaultInput,
            expectedMove: Boolean = true
    ) {
        val oneLineInput = input.toOneLineInput()
        val board = createBoard(oneLineInput)
        val rowOrColumn = getRowOrColumn(board)
        val actualMove = board.moveValuesInRowOrColumn(rowOrColumn)
        val valuesInRow = rowOrColumn.map { board[it] }

        Assert.assertEquals("Incorrect move in $valuesInRow.\nInput:    $input",
                expected.toMultiLineInput(), board.print().toMultiLineInput())

        Assert.assertEquals("The 'moveValuesInRowOrColumn' method returns incorrect result", expectedMove, actualMove)
    }
}