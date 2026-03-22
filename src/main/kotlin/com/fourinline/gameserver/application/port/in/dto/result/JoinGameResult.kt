package com.fourinline.gameserver.application.port.`in`.dto.result

import com.fourinline.gameserver.domain.GameSession

sealed interface JoinGameResult {
    data class Success(val session: GameSession) : JoinGameResult
    data object AlreadyFull : JoinGameResult
    data object CannotJoinOwnGame : JoinGameResult
    data object GameNotFound : JoinGameResult
}