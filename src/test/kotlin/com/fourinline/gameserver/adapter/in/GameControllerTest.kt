package com.fourinline.gameserver.adapter.`in`

import com.fourinline.gameserver.adapter.`in`.JoinErrorMessage.GAME_ALREADY_FULL
import com.fourinline.gameserver.adapter.`in`.JoinErrorMessage.PLAYER_CANNOT_JOIN_OWN
import com.fourinline.gameserver.application.port.out.persistence.SessionStatus
import com.fourinline.gameserver.domain.GameSessionMother.Companion.guestId
import com.fourinline.gameserver.domain.GameSessionMother.Companion.hostId
import com.fourinline.gameserver.domain.Outcome
import com.fourinline.gameserver.domain.PlayerId
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webflux.test.autoconfigure.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.*

@WebFluxTest(GameController::class)
@Import(TestConfig::class)
class GameControllerTest @Autowired constructor(val client: WebTestClient) {

    @Nested
    inner class Creation {
        @Test
        fun `should create new game`() {
            val gameId = createSession()

            val game = getGameSuccess(gameId)

            game.gameId shouldBe gameId
        }
    }

    @Nested
    inner class Joining {

        @Test
        fun `should be able to join new game`() {
            val gameId = createSession()

            val joinResponse = joinSessionSuccess(gameId)

            joinResponse.gameId shouldBe gameId
        }

        @Test
        fun `should return 404 if try to join game that does not exist`() {
            val response = joinSession(UUID.fromString("38361372-C997-4BEB-9616-63292A26CBB5"))

            response.expectStatus().isNotFound
        }

        @Test
        fun `should return 409 if game is already full`() {
            val gameId = createGameAndJoin().gameId

            joinSession(gameId).expectStatus()
                .isEqualTo(HttpStatus.CONFLICT)
                .expectBody(String::class.java).isEqualTo(GAME_ALREADY_FULL)
        }

        @Test
        fun `should return 400 if try to join own game`() {
            val gameId = createSession()

            joinSession(gameId, hostId()).expectStatus().isBadRequest
                .expectBody(String::class.java).isEqualTo(PLAYER_CANNOT_JOIN_OWN)
        }

    }

    @Nested
    inner class Game {

        @Test
        fun `should be able to play whole game when host win`() {
            val host = hostId()
            val guest = guestId()
            val gameId = createSession()
            joinSessionSuccess(gameId)

            moves(
                gameId,
                host move 1, guest move 2,
                host move 2, guest move 3,
                host move 3, guest move 3,
                host move 4, guest move 5,
                host move 4, guest move 6,
            )

            val result = moveHost(gameId, 5)

            result.status shouldBe SessionStatus.FINISHED
            result.outcome shouldBe Outcome.HOST_WON
        }

        @Test
        fun `should be able to play whole game when guest win`() {
            val host = hostId()
            val guest = guestId()
            val gameId = createSession()
            joinSessionSuccess(gameId)

            moves(
                gameId,
                host move 0, guest move 0,
                host move 1, guest move 0,
                host move 2, guest move 0,
                host move 1,
            )

            val result = moveGuest(gameId, 0)

            result.status shouldBe SessionStatus.FINISHED
            result.outcome shouldBe Outcome.GUEST_WON
        }

        @Test
        fun `should return 409 on incorrect move`() {
            val gameId = createGameAndJoin().gameId

            move(gameId, hostId(), -1).expectStatus().isEqualTo(HttpStatus.CONFLICT)
        }

        @Test
        fun `should return 404 if try to move on game that does not exist`() {
            val notExistingGameId = UUID.fromString("A6BF8066-1339-49F6-883C-1A873446370E")

            move(notExistingGameId, hostId(), 0).expectStatus().isNotFound
        }

        @Test
        fun `should return 400 if invalid request`() {
            val gameId = createGameAndJoin()
            client.post().uri("/games/$gameId/moves")
                .header("X-Player-Id", hostId().value.toString())
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest
        }

    }

    @Nested
    inner class Getting {

        @Test
        fun `should return 200 if game exists`() {
            val gameId = createSession()

            getGame(gameId).expectStatus().isOk
        }

        @Test
        fun `should return 404 if game does not exist`() {
            val response = getGame(UUID.fromString("38361372-C997-4BEB-9616-63292A26CBB5"))

            response.expectStatus().isNotFound
        }

        @Test
        fun `should return 400 if incorrect request`() {
            client.get()
                .uri("/games/102929")
                .exchange()
                .expectStatus().isBadRequest
        }

    }

    private fun getGame(gameId: UUID) = client.get()
        .uri("/games/$gameId")
        .exchange()

    private fun getGameSuccess(gameId: UUID): GameSessionResponse =
        getGame(gameId)
            .expectStatus().isOk
            .expectBody(GameSessionResponse::class.java)
            .returnResult()
            .responseBody!!

    private data class Move(val playerId: PlayerId, val columnIndex: Int)

    private infix fun PlayerId.move(columnIndex: Int): Move = Move(this, columnIndex)
    private fun moves(gameId: UUID, vararg moves: Move) {
        moves.forEach { moveSuccess(gameId, it.playerId, it.columnIndex) }
    }

    private fun createSession(): UUID =
        client
            .post()
            .uri("/games")
            .header("X-Player-Id", hostId().value.toString())
            .exchange()
            .expectStatus().isCreated
            .expectHeader().exists("Location")
            .expectBody(GameSessionResponse::class.java)
            .returnResult()
            .responseBody!!
            .gameId

    private fun joinSession(gameId: UUID, playerId: PlayerId = guestId()) =
        client.patch().uri("/games/$gameId/players")
            .header("X-Player-Id", playerId.value.toString())
            .exchange()

    private fun joinSessionSuccess(gameId: UUID): GameSessionResponse =
        joinSession(gameId)
            .expectStatus().isOk
            .expectBody(GameSessionResponse::class.java)
            .returnResult()
            .responseBody!!

    private fun createGameAndJoin(): GameSessionResponse {
        val gameId = createSession()
        return joinSessionSuccess(gameId)
    }

    private fun move(gameId: UUID, playerId: PlayerId, columnIndex: Int) = client.post().uri("/games/$gameId/moves")
        .header("X-Player-Id", playerId.value.toString())
        .bodyValue(MoveRequest(columnIndex))
        .exchange()

    private fun moveSuccess(gameId: UUID, playerId: PlayerId, columnIndex: Int): GameSessionResponse =
        move(gameId, playerId, columnIndex)
            .expectStatus().isOk
            .expectBody(GameSessionResponse::class.java)
            .returnResult()
            .responseBody!!

    private fun moveGuest(gameId: UUID, columnIndex: Int): GameSessionResponse =
        moveSuccess(gameId, guestId(), columnIndex)

    private fun moveHost(gameId: UUID, columnIndex: Int): GameSessionResponse =
        moveSuccess(gameId, hostId(), columnIndex)
}