package com.fourinline.gameserver.domain

import com.fourinline.gameserver.application.port.`in`.dto.result.JoinGameResult
import com.fourinline.gameserver.application.port.out.persistence.SessionStatus
import com.fourinline.gameserver.domain.GameSessionMother.Companion.guestId
import com.fourinline.gameserver.domain.join.JoinResult
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import java.util.*


class GameSessionTest : FunSpec({

    context("joining the game") {
        test("should succeed for new game") {
            val session = GameSessionMother.newSession()

            val result = session.join(guestId())

            result.shouldBeInstanceOf<JoinGameResult.Success>()
            result.session.shouldBeInstanceOf<GameSession.InProgress>()
            result.session.guestId shouldBe guestId()
        }

        test("should not allow player to join its own game") {
            val session = GameSessionMother.newSession()

            val result = session.join(session.hostId)

            result shouldBe JoinGameResult.CannotJoinOwnGame
        }
    }

    context("making a move") {
        test("should succeed for legal move and change turn to another player") {
            val boardMock = mockk<Board>()
            val session = GameSessionMother.fullSession(boardMock)
            every { boardMock.dropDisc(any(), any()) } returns DropResult.Success(boardMock, false)
            every { boardMock.isFull() } returns false

            val result = session.move(session.hostId, 2)

            result.shouldBeInstanceOf<MoveResult.Success>()
            result.session.shouldBeInstanceOf<GameSession.InProgress>()
            result.session.currentPlayer shouldBe PlayerSlot.GUEST
        }

        test("should finish the game if player won") {
            val boardMock = mockk<Board>()
            val session = GameSessionMother.fullSession(boardMock)
            every { boardMock.dropDisc(any(), any()) } returns DropResult.Success(boardMock, true)

            val result = session.move(session.hostId, 2)

            result.shouldBeInstanceOf<MoveResult.Success>()
            result.session.shouldBeInstanceOf<GameSession.Finished>()
            result.session.outcome shouldBe Outcome.HOST_WON
        }

        test("should fail if player try to move out of order") {
            val session = GameSessionMother.fullSession()

            val result = session.move(session.guestId, 2)

            result.shouldBeInstanceOf<MoveResult.IllegalMove>()
        }

        test("should fail if target column is full") {
            val boardMock = mockk<Board>()
            val session = GameSessionMother.fullSession(boardMock)
            every { boardMock.dropDisc(any(), any()) } returns DropResult.ColumnFull

            val result = session.move(session.hostId, 2)

            result.shouldBeInstanceOf<MoveResult.IllegalMove>()
        }

        test("should fail if move out of bounds") {
            val boardMock = mockk<Board>()
            val session = GameSessionMother.fullSession(boardMock)
            every { boardMock.dropDisc(any(), any()) } returns DropResult.OutOfBoundsDrop

            val result = session.move(session.hostId, 2)

            result.shouldBeInstanceOf<MoveResult.IllegalMove>()
        }
    }
})
