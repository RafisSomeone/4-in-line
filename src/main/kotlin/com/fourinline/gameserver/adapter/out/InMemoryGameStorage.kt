package com.fourinline.gameserver.adapter.out

import com.fourinline.gameserver.application.port.out.GameStorage
import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.GameSessionDraft
import java.util.*
import java.util.concurrent.ConcurrentHashMap

class InMemoryGameStorage: GameStorage {
    val database: MutableMap<GameId, GameSession> = ConcurrentHashMap()

    override suspend fun createGameSession(session: GameSessionDraft): GameSession = GameSession.WaitingForOpponent(
        GameId(UUID.randomUUID()), session.hostId
    ).also {
        database[it.gameId] = it
    }

    override suspend fun getGameSession(gameId: GameId): GameSession? = database[gameId]

    override suspend fun save(session: GameSession) {
        database[session.gameId] = session
    }
}