package com.fourinline.gameserver.application.port.`in`

import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession

interface GameQueryHandler {
    fun getGame(gameId: GameId): GameSession?
}