package com.rnfverse.data.mediaservice

import com.rnfverse.domain.manager.DataStoreManager
import com.rnfverse.domain.mediaservice.handler.MediaPlayerHandler
import com.rnfverse.domain.repository.AnalyticsRepository
import com.rnfverse.domain.repository.LocalPlaylistRepository
import com.rnfverse.domain.repository.SongRepository
import com.rnfverse.domain.repository.StreamRepository
import kotlinx.coroutines.CoroutineScope

expect fun createMediaServiceHandler(
    dataStoreManager: DataStoreManager,
    songRepository: SongRepository,
    streamRepository: StreamRepository,
    localPlaylistRepository: LocalPlaylistRepository,
    analyticsRepository: AnalyticsRepository,
    coroutineScope: CoroutineScope,
): MediaPlayerHandler