package com.fourinline.gameserver.adapter.`in`

import com.fourinline.gameserver.domain.GameSession
import java.util.*

data class GameSessionResponse(val gameId: UUID)

fun GameSession.toResponse() = GameSessionResponse(this.gameId.value)