package com.fourinline.gameserver.application.usecase

import com.fourinline.gameserver.application.port.`in`.GameCommandHandler
import com.fourinline.gameserver.application.port.`in`.dto.command.CreateGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.JoinGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.MoveCommand
import com.fourinline.gameserver.application.port.`in`.dto.result.JoinGameResult
import com.fourinline.gameserver.application.port.out.GameStorage
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.GameSessionDraft
import com.fourinline.gameserver.domain.MoveResult

class GameCommandService(private val storage: GameStorage) : GameCommandHandler {

    override fun createGame(command: CreateGameCommand): GameSession {
        val session = storage.createGameSession(GameSessionDraft(command.hostId))
        return GameSession.WaitingForOpponent(session.gameId, session.hostId)
    }

    override fun joinGameSession(command: JoinGameCommand): JoinGameResult {
        val session = storage.getGameSession(command.gameId) ?: return JoinGameResult.GameNotFound

        return when(session) {
            is GameSession.WaitingForOpponent -> session.join(command.guestId)
            is GameSession.InProgress -> JoinGameResult.AlreadyFull
            is GameSession.Finished -> JoinGameResult.AlreadyFull
        }
    }

    override fun move(command: MoveCommand): MoveResult {
        val session = storage.getGameSession(command.gameId) ?: return MoveResult.GameNotFound

        return when(session) {
            is GameSession.InProgress -> session.move(command.playerId, command.columnIndex)
            is GameSession.WaitingForOpponent -> MoveResult.IllegalMove(MoveResult.Reason.WAITING_FOR_OPPONENT)
            is GameSession.Finished -> MoveResult.IllegalMove(MoveResult.Reason.GAME_FINISHED)
        }
    }
}