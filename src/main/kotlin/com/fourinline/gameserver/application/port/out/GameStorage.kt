package com.fourinline.gameserver.application.port.out

import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.GameSession
import com.fourinline.gameserver.domain.GameSessionDraft

interface GameStorage {
    fun createGameSession(session: GameSessionDraft): GameSession
    fun getGameSession(gameId: GameId): GameSession?
    fun save(session: GameSession)
}