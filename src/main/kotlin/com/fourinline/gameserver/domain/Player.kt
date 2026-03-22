package com.fourinline.gameserver.domain

import java.util.UUID

data class Player(val id: PlayerId)

@JvmInline
value class PlayerId(val value: UUID)
