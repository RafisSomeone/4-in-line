package com.fourinline.gameserver.domain

sealed interface MoveResult {
    data class Success(val session: GameSession) : MoveResult
    data class IllegalMove(val reason: Reason) : MoveResult
    data object GameNotFound : MoveResult

    enum class Reason {
        NOT_YOUR_TURN,
        COLUMN_FULL,
        MOVE_OUT_OF_BOUNDS,
        WAITING_FOR_OPPONENT,
        GAME_FINISHED
    }
}