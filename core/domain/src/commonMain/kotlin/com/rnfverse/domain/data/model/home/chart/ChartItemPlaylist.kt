package com.rnfverse.domain.data.model.home.chart

import com.rnfverse.domain.data.model.browse.artist.ResultPlaylist

data class ChartItemPlaylist(
    val title: String,
    val playlists: List<ResultPlaylist>,
)