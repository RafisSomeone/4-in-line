package com.fourinline.gameserver.adapter.`in`

import io.kotest.matchers.shouldBe
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import tools.jackson.databind.ObjectMapper
import java.util.UUID

@WebMvcTest(GameController::class)
@Import(TestConfig::class)
class GameControllerTest @Autowired constructor(val mockMvc: MockMvc, val mapper: ObjectMapper) {

    @Test
    fun `should create new game`() {
        val postResponse = createSession()
        val createdGameId = postResponse.gameId

        val game = getGame(createdGameId)

        game.gameId shouldBe createdGameId
    }

    @Test
    fun `should be able to join new game`() {
        val createResponse = createSession()

        val joinResponse = joinSession(createResponse.gameId)

        createResponse.gameId shouldBe joinResponse.gameId
    }

    private fun getGame(gameId: UUID): GameSessionResponse = extractResponse(mockMvc.get("/games/$gameId").andExpect {
            status { isOk() }
            jsonPath("$.gameId", `is`(gameId.toString()))
        }.andReturn())

    private fun createSession(): GameSessionResponse = extractResponse(mockMvc.post("/games") {
        header("X-Player-Id", "86581346-DFAF-4CA2-B2F2-45282918F7F4")
    }.andExpect {
        status { isCreated() }
        content { contentTypeCompatibleWith(MediaType.APPLICATION_JSON) }
        jsonPath("$.gameId") { isString() }
    }.andReturn())

    private fun joinSession(gameId: UUID): GameSessionResponse = extractResponse(mockMvc.patch("/games/$gameId/players") {
        header("X-Player-Id", "ACE3A674-A50B-48DF-A762-2FC58A434D73")
    }.andExpect {
        status { isOk() }
        jsonPath("$.gameId", `is`(gameId.toString()))
    }.andReturn())

    private fun move(gameId: UUID): GameSessionResponse = extractResponse(mockMvc.post("/games/$gameId/moves") {
        header("X-Player-Id", "86581346-DFAF-4CA2-B2F2-45282918F7F4")
    }.andExpect {
        status { isOk() }
    }.andReturn())

    private inline fun <reified T> extractResponse(result: MvcResult): T = mapper.readValue(result.response.contentAsString, T::class.java)
}