package com.fourinline.gameserver.domain

import com.fourinline.gameserver.application.port.`in`.dto.result.JoinGameResult
import com.fourinline.gameserver.domain.MoveResult.Reason
import java.util.*

sealed interface GameSession {

    val gameId: GameId
    val hostId: PlayerId

    companion object {
        private const val DEFAULT_BOARD_SIZE = 10
    }

    data class WaitingForOpponent(
        override val gameId: GameId,
        override val hostId: PlayerId,
    ) : GameSession {
        fun join(guestId: PlayerId): JoinGameResult {
            if (hostId == guestId) return JoinGameResult.CannotJoinOwnGame

            return toInProgress(guestId).let(JoinGameResult::Success)
        }

        private fun toInProgress(guestId: PlayerId): InProgress = InProgress(
            gameId = gameId,
            hostId = hostId,
            board = Board.empty(DEFAULT_BOARD_SIZE, DEFAULT_BOARD_SIZE),
            currentPlayer = PlayerSlot.HOST,
            guestId = guestId
        )
    }

    data class InProgress(
        override val gameId: GameId,
        override val hostId: PlayerId,
        val board: Board,
        val currentPlayer: PlayerSlot,
        val guestId: PlayerId

    ) : GameSession {

        fun currentPlayerId(): PlayerId = when (currentPlayer) {
            PlayerSlot.HOST -> hostId
            PlayerSlot.GUEST -> guestId
        }

        fun move(playerId: PlayerId, columnIndex: Int): MoveResult {
            if (currentPlayerId() != playerId) return MoveResult.IllegalMove(Reason.NOT_YOUR_TURN)

            return when (val result = board.dropDisc(columnIndex, currentPlayer)) {
                is DropResult.Success -> when {
                    result.hasWinningLane -> toFinish(result.board, currentPlayer.toOutcome())
                    result.board.isFull() -> toFinish(result.board, Outcome.DRAW)
                    else -> copy(board = result.board, currentPlayer = currentPlayer.opposite())
                }.let(MoveResult::Success)

                DropResult.ColumnFull -> MoveResult.IllegalMove(Reason.COLUMN_FULL)
                DropResult.OutOfBoundsDrop -> MoveResult.IllegalMove(Reason.MOVE_OUT_OF_BOUNDS)
            }
        }

        private fun toFinish(board: Board, outcome: Outcome) =
            Finished(gameId = gameId, hostId = hostId, board = board, guestId = guestId, outcome = outcome)
    }

    data class Finished(
        override val gameId: GameId,
        override val hostId: PlayerId,
        val board: Board,
        val guestId: PlayerId,
        val outcome: Outcome
    ) : GameSession
}

@JvmInline
value class GameId(val value: UUID) {
    companion object {
        fun random(): GameId = GameId(UUID.randomUUID())
    }
}
