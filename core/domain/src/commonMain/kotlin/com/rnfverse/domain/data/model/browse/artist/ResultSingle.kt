package com.rnfverse.domain.data.model.browse.artist

import com.rnfverse.domain.data.model.searchResult.songs.Thumbnail
import com.rnfverse.domain.data.type.HomeContentType

data class ResultSingle(
    val browseId: String,
    val thumbnails: List<Thumbnail>,
    val title: String,
    val year: String,
) : HomeContentType