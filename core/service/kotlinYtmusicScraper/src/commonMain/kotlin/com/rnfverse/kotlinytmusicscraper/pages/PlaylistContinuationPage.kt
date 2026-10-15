package com.rnfverse.kotlinytmusicscraper.pages

import com.rnfverse.kotlinytmusicscraper.models.SongItem

data class PlaylistContinuationPage(
    val songs: List<SongItem>,
    val continuation: String?,
)