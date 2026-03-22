package com.fourinline.gameserver.domain

enum class PlayerSlot {
    HOST,
    GUEST;

    fun opposite() = when (this) {
        HOST -> GUEST
        GUEST -> HOST
    }

    fun toOutcome() = when (this) {
        HOST -> Outcome.HOST_WON
        GUEST -> Outcome.GUEST_WON
    }
}