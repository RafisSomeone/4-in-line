package com.fourinline.gameserver.application.port.out.dto.result

import com.fourinline.gameserver.domain.GameSession

sealed interface AddSecondPlayerResult {
    data class Success(val session: GameSession) : AddSecondPlayerResult
    data object GameNotFound : AddSecondPlayerResult
    data object AlreadyHasSecondPlayer : AddSecondPlayerResult
}