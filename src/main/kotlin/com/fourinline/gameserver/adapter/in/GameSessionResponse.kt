package com.fourinline.gameserver.adapter.`in`

import com.fourinline.gameserver.application.port.out.persistence.SessionStatus
import com.fourinline.gameserver.domain.Board
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.Outcome
import com.fourinline.gameserver.domain.PlayerSlot
import java.util.UUID

data class GameSessionResponse(val gameId: UUID, val status: SessionStatus, val currentPlayer: PlayerSlot?, val board: List<List<PlayerSlot?>>?, val outcome: Outcome?)

fun GameSession.toResponse(): GameSessionResponse = when (this) {
    is GameSession.InProgress -> GameSessionResponse(this.gameId.value, SessionStatus.IN_PROGRESS, this.currentPlayer, this.board.toResponse(), null)
    is GameSession.Finished -> GameSessionResponse(this.gameId.value, SessionStatus.FINISHED, null, this.board.toResponse(), this.outcome)
    is GameSession.WaitingForOpponent -> GameSessionResponse(this.gameId.value, SessionStatus.WAITING_FOR_OPPONENT, null, null, null)
}

fun Board.toResponse() = List(width) { column ->  List(height) { row -> this[column, row] } }