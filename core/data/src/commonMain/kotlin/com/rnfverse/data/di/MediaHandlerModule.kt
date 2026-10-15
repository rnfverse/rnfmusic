package com.rnfverse.data.di

import com.rnfverse.common.Config
import com.rnfverse.data.mediaservice.createMediaServiceHandler
import com.rnfverse.domain.mediaservice.handler.MediaPlayerHandler
import kotlinx.coroutines.CoroutineScope
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mediaHandlerModule =
    module {
        single<MediaPlayerHandler>(createdAtStart = true) {
            createMediaServiceHandler(
                dataStoreManager = get(),
                songRepository = get(),
                streamRepository = get(),
                localPlaylistRepository = get(),
                analyticsRepository = get(),
                coroutineScope = get<CoroutineScope>(named(Config.SERVICE_SCOPE)),
            )
        }
    }