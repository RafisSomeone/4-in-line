package com.fourinline.gameserver.application.port.out.persistence

import com.fourinline.gameserver.domain.Outcome
import com.fourinline.gameserver.domain.PlayerSlot
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table("game_session")
class GameSessionEntity(
    @Id val gameId: UUID,
    val hostId: UUID,
    val guestId: UUID?,
    val status: SessionStatus,
    val currentPlayer: PlayerSlot?,
    val board: String?,
    val boardWidth: Int?,
    val boardHeight: Int?,
    val outcome: Outcome?,
    @Version val version: Int
)
