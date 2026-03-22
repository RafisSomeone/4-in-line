package com.fourinline.gameserver.config

import com.fourinline.gameserver.adapter.out.InMemoryGameStorage
import com.fourinline.gameserver.application.port.`in`.GameCommandHandler
import com.fourinline.gameserver.application.port.`in`.GameQueryHandler
import com.fourinline.gameserver.application.port.out.GameStorage
import com.fourinline.gameserver.application.usecase.GameCommandService
import com.fourinline.gameserver.application.usecase.GameQueryService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ProductionConfig {
    @Bean
    fun getGameCommandHandler(storage: GameStorage): GameCommandHandler = GameCommandService(storage)

    @Bean
    fun getGameQueryHandler(storage: GameStorage): GameQueryHandler = GameQueryService(storage)

    @Bean
    fun getStorage(): GameStorage = InMemoryGameStorage()
}