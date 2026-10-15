package com.rnfverse.data.mediaservice

actual fun createMediaServiceHandler(
    dataStoreManager: com.rnfverse.domain.manager.DataStoreManager,
    songRepository: com.rnfverse.domain.repository.SongRepository,
    streamRepository: com.rnfverse.domain.repository.StreamRepository,
    localPlaylistRepository: com.rnfverse.domain.repository.LocalPlaylistRepository,
    analyticsRepository: com.rnfverse.domain.repository.AnalyticsRepository,
    coroutineScope: kotlinx.coroutines.CoroutineScope,
): com.rnfverse.domain.mediaservice.handler.MediaPlayerHandler {
    TODO("Not yet implemented")
}