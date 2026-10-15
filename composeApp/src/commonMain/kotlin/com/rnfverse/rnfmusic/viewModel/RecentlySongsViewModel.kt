package com.rnfverse.rnfmusic.viewModel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.rnfverse.domain.repository.SongRepository
import com.rnfverse.rnfmusic.pagination.RecentPagingSource
import com.rnfverse.rnfmusic.viewModel.base.BaseViewModel

class RecentlySongsViewModel(
    private val songRepository: SongRepository,
) : BaseViewModel() {
    val recentlySongs =
        Pager(
            PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 20,
            ),
        ) {
            RecentPagingSource(songRepository)
        }.flow.cachedIn(viewModelScope)
}