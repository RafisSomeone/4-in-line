package com.fourinline.gameserver.domain.join

import com.fourinline.gameserver.domain.GameSession

sealed interface JoinResult {
    data class Success(val session: GameSession) : JoinResult
    data object CannotJoinOwnGame : JoinResult
}