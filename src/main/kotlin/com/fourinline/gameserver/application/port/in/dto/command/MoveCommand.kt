package com.fourinline.gameserver.application.port.`in`.dto.command

import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.PlayerId

data class MoveCommand(val columnIndex: Int, val playerId: PlayerId, val gameId: GameId)
