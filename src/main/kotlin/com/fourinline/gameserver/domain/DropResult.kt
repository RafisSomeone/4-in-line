package com.fourinline.gameserver.domain

sealed interface DropResult {
    data class Success(val board: Board, val hasWinningLane: Boolean) : DropResult
    data object OutOfBoundsDrop: DropResult
    data object ColumnFull: DropResult
}