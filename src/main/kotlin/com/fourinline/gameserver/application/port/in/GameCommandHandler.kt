package com.fourinline.gameserver.application.port.`in`

import com.fourinline.gameserver.application.port.`in`.dto.command.CreateGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.JoinGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.MoveCommand
import com.fourinline.gameserver.application.port.`in`.dto.result.JoinGameResult
import com.fourinline.gameserver.domain.MoveResult
import com.fourinline.gameserver.domain.GameSession

interface GameCommandHandler {
    fun createGame(command: CreateGameCommand): GameSession
    fun joinGameSession(command: JoinGameCommand): JoinGameResult
    fun move(command: MoveCommand): MoveResult
}