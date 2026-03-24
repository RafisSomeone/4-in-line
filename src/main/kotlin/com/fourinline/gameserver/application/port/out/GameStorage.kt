package com.fourinline.gameserver.application.port.out

import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.GameSessionDraft

interface GameStorage {
    suspend fun createGameSession(session: GameSessionDraft): GameSession
    suspend fun getGameSession(gameId: GameId): GameSession?
    suspend fun save(session: GameSession)
}