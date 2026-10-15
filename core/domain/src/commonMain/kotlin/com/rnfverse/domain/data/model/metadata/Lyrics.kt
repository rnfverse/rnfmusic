package com.rnfverse.domain.data.model.metadata

import kotlinx.serialization.Serializable

@Serializable
data class Lyrics(
    val error: Boolean = false,
    val lines: List<Line>?,
    val syncType: String?,
    val rnfMusicLyrics: RnfMusicLyrics? = null,
)

@Serializable
data class RnfMusicLyrics(
    val id: String,
    val vote: Int,
)