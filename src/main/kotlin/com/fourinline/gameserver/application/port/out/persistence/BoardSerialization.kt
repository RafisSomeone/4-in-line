package com.fourinline.gameserver.application.port.out.persistence

import com.fourinline.gameserver.domain.Board
import com.fourinline.gameserver.domain.PlayerSlot

fun String.deserialize(width: Int, height:Int): Board {
    val grid = List(width) { column -> List(height) {
            row ->
        when (this[column * height + row]) {
            'H' -> PlayerSlot.HOST
            'G' -> PlayerSlot.GUEST
            '.' -> null
            else -> throw IllegalArgumentException("Invalid cell")
        }
    } }

    return Board.of(width, height, grid)
}

fun Board.serialize(): String = buildString {
    for (column in 0..<width) {
        for (row in 0..<height) {
            append(
                when(this@serialize[column, row]) {
                    PlayerSlot.HOST -> 'H'
                    PlayerSlot.GUEST -> 'G'
                    null -> '.'
                }
            )
        }
    }
}
