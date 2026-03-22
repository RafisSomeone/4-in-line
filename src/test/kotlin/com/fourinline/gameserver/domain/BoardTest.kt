package com.fourinline.gameserver.domain

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

class BoardTest : FunSpec({

    fun Board.expectDropResult(column: Int, row: Int, spot: PlayerSlot, hasWinningLane: Boolean): Board {
        val success = dropDisc(column, spot).shouldBeInstanceOf<DropResult.Success>()
        success.hasWinningLane shouldBe hasWinningLane
        success.board[column, row] shouldBe spot
        return success.board
    }

    test("should not create board with height less than 1") {
        shouldThrowExactly<IllegalArgumentException> {
            Board.empty(height = 0, width = 7)
        }
    }

    test("should not create board with width less than 1") {
        shouldThrowExactly<IllegalArgumentException> {
            Board.empty(height = 7, width = 0)
        }
    }

    test("should place disc in a first empty row of the column") {
        val board = Board.empty(height = 7, width = 6)
        val targetColumn = 2

        val newBoard = board.expectDropResult(targetColumn, 0, PlayerSlot.HOST, false)
        .expectDropResult(targetColumn, 1, PlayerSlot.GUEST, false)
        newBoard[targetColumn, 2] shouldBe null
    }

    test("should reject dropping outside of the board") {
        val board = Board.empty(height = 7, width = 6)

        val result = board.dropDisc(columnIndex = 10, slot = PlayerSlot.HOST)

        result shouldBe DropResult.OutOfBoundsDrop
    }

    test("should allow valid drop without winner") {
        val board = Board.empty(height = 7, width = 6)

        board.expectDropResult(1, 0, PlayerSlot.HOST, false)
    }

    test("should reject drop into full column") {
        val board = Board.empty(height = 1, width = 6)
        val targetColumn = 1
        board.expectDropResult(targetColumn, 0, PlayerSlot.HOST, false)

        .dropDisc(columnIndex = targetColumn, slot = PlayerSlot.HOST) shouldBe DropResult.ColumnFull
    }

    test("should detect horizontal win") {
        val board = Board.empty(height = 7, width = 6)
        board.expectDropResult(1, 0,PlayerSlot.HOST, false)
        .expectDropResult(2, 0,PlayerSlot.HOST, false)
        .expectDropResult(3, 0, PlayerSlot.HOST, false)

        .expectDropResult(4, 0, PlayerSlot.HOST, true)
    }

    test("should detect vertical win") {
        val board = Board.empty(height = 7, width = 6)
        val targetColumn = 1
        board.expectDropResult(targetColumn, 0,PlayerSlot.HOST, false)
        .expectDropResult(targetColumn, 1, PlayerSlot.HOST, false)
        .expectDropResult(targetColumn, 2,PlayerSlot.HOST, false)

        .expectDropResult(targetColumn, 3, PlayerSlot.HOST, true)
    }

    test("should detect diagonal win ascending right") {
        val board = Board.empty(height = 7, width = 6)
        board.expectDropResult(1, 0, PlayerSlot.HOST, false)

        .expectDropResult(2, 0,PlayerSlot.GUEST, false)
        .expectDropResult(2, 1,PlayerSlot.HOST, false)

        .expectDropResult(3, 0,PlayerSlot.GUEST, false)
        .expectDropResult(3, 1,PlayerSlot.GUEST, false)
        .expectDropResult(3, 2, PlayerSlot.HOST, false)

        .expectDropResult(4, 0,PlayerSlot.GUEST, false)
        .expectDropResult(4, 1,PlayerSlot.GUEST, false)
        .expectDropResult(4, 2, PlayerSlot.GUEST, false)

        .expectDropResult(4, 3,PlayerSlot.HOST, true)
    }

    test("should detect diagonal win descending right") {
        val board = Board.empty(height = 7, width = 6)

        board.expectDropResult(1, 0,PlayerSlot.GUEST, false)
        .expectDropResult(1, 1,PlayerSlot.GUEST, false)
        .expectDropResult(1, 2, PlayerSlot.GUEST, false)
        .expectDropResult(1, 3, PlayerSlot.HOST, false)

        .expectDropResult(2, 0,PlayerSlot.GUEST, false)
        .expectDropResult(2, 1, PlayerSlot.GUEST, false)
        .expectDropResult(2, 2, PlayerSlot.HOST, false)

        .expectDropResult(3, 0, PlayerSlot.GUEST, false)
        .expectDropResult(3, 1, PlayerSlot.HOST, false)

        .expectDropResult(4, 0, PlayerSlot.HOST, true)
    }
})