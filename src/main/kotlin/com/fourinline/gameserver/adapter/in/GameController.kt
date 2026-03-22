package com.fourinline.gameserver.adapter.`in`

import com.fourinline.gameserver.application.port.`in`.GameCommandHandler
import com.fourinline.gameserver.application.port.`in`.GameQueryHandler
import com.fourinline.gameserver.application.port.`in`.dto.command.CreateGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.JoinGameCommand
import com.fourinline.gameserver.application.port.`in`.dto.command.MoveCommand
import com.fourinline.gameserver.application.port.`in`.dto.result.JoinGameResult
import com.fourinline.gameserver.domain.GameId
import com.fourinline.gameserver.domain.MoveResult
import com.fourinline.gameserver.domain.PlayerId
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest
import java.util.*

@RestController
@RequestMapping("/games")
class GameController(private val commandHandler: GameCommandHandler, private val queryHandler: GameQueryHandler) {

    @PostMapping
    fun createGame(@RequestHeader("X-Player-Id") playerId: UUID): ResponseEntity<GameSessionResponse> =
        commandHandler.createGame(CreateGameCommand(PlayerId(playerId)))
            .let {
                val location = fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(it.gameId.value)
                    .toUri()
                ResponseEntity.created(location).body(GameSessionResponse(it.gameId.value))
            }

    @PatchMapping("/{id}/players")
    fun joinGameSession(
        @RequestHeader("X-Player-Id") playerId: UUID,
        @PathVariable("id") gameId: UUID
    ): ResponseEntity<*> =
        when (val result = commandHandler.joinGameSession(JoinGameCommand(GameId(gameId), PlayerId(playerId)))) {
            is JoinGameResult.Success -> ResponseEntity.ok(result.session.toResponse())
            JoinGameResult.GameNotFound -> ResponseEntity.notFound().build<GameSessionResponse>()
            JoinGameResult.AlreadyFull -> ResponseEntity.status(HttpStatus.CONFLICT).body("Game is already full")
            JoinGameResult.CannotJoinOwnGame -> ResponseEntity.badRequest().body("Player cannot join its own game")
        }

    @GetMapping("/{id}")
    fun getGame(@PathVariable id: UUID): ResponseEntity<GameSessionResponse> = queryHandler.getGame(GameId(id))
        ?.let { ResponseEntity.ok(it.toResponse()) }
        ?: ResponseEntity.notFound().build()

    @PostMapping("/{id}/moves")
    fun move(
        @RequestHeader("X-Player-Id") playerId: UUID,
        @PathVariable("id") gameId: UUID,
        @RequestBody moveRequest: MoveRequest
    ): ResponseEntity<*> = when (val result = commandHandler.move(
        MoveCommand(moveRequest.columnIndex, PlayerId(playerId), GameId(gameId))
    )) {
        is MoveResult.Success -> ResponseEntity.ok(result.session.toResponse())
        is MoveResult.IllegalMove -> ResponseEntity.status(HttpStatus.CONFLICT).body(result.reason.name)
        MoveResult.GameNotFound -> ResponseEntity.notFound().build<GameSessionResponse>()
    }

}