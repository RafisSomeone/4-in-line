package com.fourinline.gameserver.application.port.`in`.dto.command

import com.fourinline.gameserver.domain.PlayerId

data class CreateGameCommand(val hostId: PlayerId)