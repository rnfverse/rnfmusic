package com.rnfverse.domain.data.model.mood.genre

import com.rnfverse.domain.data.model.searchResult.songs.Thumbnail
import com.rnfverse.domain.data.type.HomeContentType

data class Content(
    val playlistBrowseId: String,
    val thumbnail: List<Thumbnail>?,
    val title: Title,
) : HomeContentType