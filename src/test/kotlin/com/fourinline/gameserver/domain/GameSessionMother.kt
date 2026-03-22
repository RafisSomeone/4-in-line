package com.fourinline.gameserver.domain

import java.util.*

class GameSessionMother {
    companion object {
        fun hostId() = PlayerId(UUID.fromString("03827CFA-8021-4FDB-8848-980401836500"))
        fun gameId() = GameId(UUID.fromString("D80BAA4D-EBFE-4330-BA4F-CE1B1163066D"))
        fun board() = Board.empty(10, 10)
        fun guestId() = PlayerId(UUID.fromString("30D27919-5425-453C-9708-BE9B8F77A57B"))

        fun newSession(): GameSession.WaitingForOpponent = GameSession.WaitingForOpponent(gameId(), hostId())
        fun fullSession(board: Board = board()): GameSession.InProgress = GameSession.InProgress(gameId(), hostId(), board, PlayerSlot.HOST, guestId())
    }
}