package com.fourinline.gameserver.application.port.out.persistence

import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.PlayerId
import kotlin.reflect.KProperty0

fun GameSessionEntity.toDomain(): GameSession = when (this.status) {
    SessionStatus.WAITING_FOR_OPPONENT -> GameSession.WaitingForOpponent(
        gameId = GameId(gameId),
        hostId = PlayerId(hostId)
    )

    SessionStatus.IN_PROGRESS -> GameSession.InProgress(
        gameId = GameId(gameId),
        hostId = PlayerId(hostId),
        board = requiredField(::board).deserialize(requiredField(::boardWidth), requiredField(::boardHeight)),
        currentPlayer = requiredField(::currentPlayer),
        guestId = PlayerId(requiredField(::guestId))
    )

    SessionStatus.FINISHED -> GameSession.Finished(
        gameId = GameId(gameId),
        hostId = PlayerId(hostId),
        board = requiredField(::board).deserialize(requiredField(::boardWidth), requiredField(::boardHeight)),
        guestId = PlayerId(requiredField(::guestId)),
        outcome = requiredField(::outcome)
    )
}

fun GameSession.toEntity(version: Int): GameSessionEntity = when (this) {
    is GameSession.WaitingForOpponent -> GameSessionEntity(
        gameId = gameId.value,
        hostId = hostId.value,
        guestId = null,
        status = SessionStatus.WAITING_FOR_OPPONENT,
        currentPlayer = null,
        board = null,
        boardWidth = null,
        boardHeight = null,
        outcome = null,
        version = version
    )

    is GameSession.InProgress -> GameSessionEntity(
        gameId = gameId.value,
        hostId = hostId.value,
        guestId = guestId.value,
        status = SessionStatus.IN_PROGRESS,
        currentPlayer = currentPlayer,
        board = board.serialize(),
        boardWidth = board.width,
        boardHeight = board.height,
        outcome = null,
        version = version
    )

    is GameSession.Finished -> GameSessionEntity(
        gameId = gameId.value,
        hostId = hostId.value,
        guestId = guestId.value,
        status = SessionStatus.FINISHED,
        currentPlayer = null,
        board = board.serialize(),
        boardWidth = board.width,
        boardHeight = board.height,
        outcome = outcome,
        version = version
    )
}

private fun <T> requiredField(property: KProperty0<T?>): T = requireNotNull(property.get()) { "Required field ${property.name} null" }
