package com.fourinline.gameserver.domain


class Board private constructor(
    val width: Int, val height: Int, private val grid: List<List<PlayerSlot?>>, private val remainingMoves: Int
) {

    init {
        require(height > 0) { "height must be greater than 0" }
        require(width > 0) { "width must be greater than 0" }
    }

    companion object {
        private const val IN_A_ROW_WIN_CONDITION = 4
        private val DIRECTIONS = listOf(0 to 1, 1 to 0, 1 to 1, -1 to 1)

        fun empty(width: Int, height: Int) = Board(
            width = width,
            height = height,
            grid = List(width) { List(height) { null } },
            remainingMoves = width * height
        )

        fun of(width:Int, height: Int, grid: List<List<PlayerSlot?>>): Board {
            val remainingMoves = grid.sumOf { column -> column.count { it != null } }

            return Board(width, height, grid, remainingMoves)
        }
    }

    operator fun get(column: Int, row: Int): PlayerSlot? = grid[column][row]

    fun dropDisc(columnIndex: Int, slot: PlayerSlot): DropResult {
        if (columnIndex !in 0..<width) return DropResult.OutOfBoundsDrop

        val column = grid[columnIndex]
        val firstEmptyRow = column.indexOfFirst { it == null }
        if (firstEmptyRow == -1) return DropResult.ColumnFull

        val newGrid = grid.map { it.toMutableList() }

        newGrid[columnIndex][firstEmptyRow] = slot

        val newBoard = Board(width, height, newGrid, remainingMoves - 1)

        return DropResult.Success(
            hasWinningLane = newBoard.hasWinningLane(columnIndex, firstEmptyRow, slot), board = newBoard
        )
    }

    private fun hasWinningLane(column: Int, row: Int, slot: PlayerSlot): Boolean =
        DIRECTIONS.any { (directionColumn, directionRow) ->
            countInOrientation(
                column, row, slot, directionColumn, directionRow
            ) >= IN_A_ROW_WIN_CONDITION
        }

    fun isFull(): Boolean = remainingMoves == 0

    private fun countInOrientation(column: Int, row: Int, slot: PlayerSlot, directionColumn: Int, directionRow: Int) =
        1 + countInDirection(column, row, slot, directionColumn, directionRow) + countInDirection(
            column, row, slot, -directionColumn, -directionRow
        )

    private fun countInDirection(
        column: Int, row: Int, slot: PlayerSlot, directionColumn: Int, directionRow: Int
    ): Int {
        var counter = 0
        var resultColumn = column + directionColumn
        var resultRow = row + directionRow

        while (resultColumn in 0..<width && resultRow in 0..<height && grid[resultColumn][resultRow] == slot) {
            counter++
            resultColumn += directionColumn
            resultRow += directionRow
        }

        return counter
    }

}