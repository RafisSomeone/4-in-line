package com.fourinline.gameserver.application.port.out.persistence

import com.fourinline.gameserver.application.port.out.persistence.SessionStatus
import com.fourinline.gameserver.domain.Outcome
import com.fourinline.gameserver.domain.PlayerSlot
import java.util.UUID

class GameSessionEntity(
    val gameId: UUID,
    val hostId: UUID,
    val guestId: UUID?,
    val status: SessionStatus,
    val currentPlayer: PlayerSlot?,
    val board: String?,
    val boardWidth: Int?,
    val boardHeight: Int?,
    val outcome: Outcome?,
    val version: Int
)
