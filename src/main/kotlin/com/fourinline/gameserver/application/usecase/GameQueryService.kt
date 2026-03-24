package com.fourinline.gameserver.application.usecase

import com.fourinline.gameserver.application.port.`in`.GameQueryHandler
import com.fourinline.gameserver.application.port.out.GameStorage
import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession

class GameQueryService(private val storage: GameStorage) : GameQueryHandler {
    override suspend fun getGame(gameId: GameId): GameSession? = storage.getGameSession(gameId)
}