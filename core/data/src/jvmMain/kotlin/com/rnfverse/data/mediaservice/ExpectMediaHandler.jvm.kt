package com.rnfverse.data.mediaservice

import com.rnfverse.domain.repository.AnalyticsRepository

actual fun createMediaServiceHandler(
    dataStoreManager: com.rnfverse.domain.manager.DataStoreManager,
    songRepository: com.rnfverse.domain.repository.SongRepository,
    streamRepository: com.rnfverse.domain.repository.StreamRepository,
    localPlaylistRepository: com.rnfverse.domain.repository.LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): com.rnfverse.domain.mediaservice.handler.MediaPlayerHandler =
    JvmMediaPlayerHandlerImpl(
        dataStoreManager = dataStoreManager,
        songRepository = songRepository,
        streamRepository = streamRepository,
        localPlaylistRepository = localPlaylistRepository,
        analyticsRepository = analyticsRepository,
        coroutineScope = coroutineScope,
    )