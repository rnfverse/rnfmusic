package com.rnfverse.rnfmusic.viewModel

import androidx.lifecycle.viewModelScope
import com.rnfverse.common.Config
import com.rnfverse.domain.data.model.browse.album.Track
import com.rnfverse.domain.mediaservice.handler.PlaylistType
import com.rnfverse.domain.mediaservice.handler.QueueData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import com.rnfverse.domain.data.model.searchResult.albums.AlbumsResult
import com.rnfverse.domain.repository.AlbumRepository
import com.rnfverse.rnfmusic.viewModel.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import rnfmusic.composeapp.generated.resources.Res
import rnfmusic.composeapp.generated.resources.error
import rnfmusic.composeapp.generated.resources.playlist_is_empty

class MoreAlbumsViewModel(
    private val albumRepository: AlbumRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<MoreAlbumsUIState>(MoreAlbumsUIState.Loading)
    val uiState: StateFlow<MoreAlbumsUIState> get() = _uiState

    private val _isPreparingPlay = MutableStateFlow(false)
    val isPreparingPlay: StateFlow<Boolean> get() = _isPreparingPlay

    /**
     * Plays this artist's whole discography, release by release in the order the page lists them,
     * each release in its own track order. Only the artist's own songs go in: a track is kept when
     * it credits this artist (by id or name) or credits nobody at all. The queue is a fixed list
     * (no continuation, no radio), so nothing from other artists is appended after it ends.
     */
    // Not tied to the page: the rest of the discography keeps being queued even if you leave it.
    private val discographyScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var discographyJob: Job? = null

    fun playDiscography(artistId: String?) {
        val state = _uiState.value as? MoreAlbumsUIState.Success ?: return
        if (_isPreparingPlay.value) return
        // The page id looks like "MPAD<channelId>"; track credits carry the bare channel id.
        val channelId = artistId?.removePrefix("MPAD")
        val queueId = "discography:${channelId ?: state.title}"
        discographyJob?.cancel()
        _isPreparingPlay.value = true
        discographyJob =
            discographyScope.launch {
                try {
                    // All releases are requested at once (4 at a time) but consumed in page order, so
                    // the first release starts playing as soon as it arrives and the rest are added
                    // to the queue behind it while it plays.
                    val gate = Semaphore(4)
                    val fetches =
                        state.albumItems.map { album ->
                            async {
                                gate.withPermit {
                                    albumRepository
                                        .getAlbumData(album.browseId)
                                        .firstOrNull()
                                        ?.data
                                        ?.tracks
                                        .orEmpty()
                                }
                            }
                        }
                    val seen = HashSet<String>()
                    var strict = true
                    var started = false
                    for (fetch in fetches) {
                        val fresh = fetch.await().filter { seen.add(it.videoId) }
                        var batch =
                            if (strict) {
                                fresh.filter { track ->
                                    val credited = track.artists.orEmpty()
                                    credited.isEmpty() ||
                                        credited.any {
                                            (channelId != null && it.id == channelId) ||
                                                it.name.equals(state.title, ignoreCase = true)
                                        }
                                }
                            } else {
                                fresh
                            }
                        if (!started && batch.isEmpty() && fresh.isNotEmpty()) {
                            // Credits could not be matched to this artist at all: trust the page.
                            strict = false
                            batch = fresh
                        }
                        if (batch.isEmpty()) continue
                        if (!started) {
                            started = true
                            setQueueData(
                                QueueData.Data(
                                    listTracks = ArrayList(batch),
                                    firstPlayedTrack = batch.first(),
                                    playlistId = queueId,
                                    playlistName = state.title,
                                    // A plain list: nothing is appended after the last song by itself.
                                    playlistType = PlaylistType.PLAYLIST,
                                    continuation = null,
                                ),
                            )
                            loadMediaItem(batch.first(), Config.ALBUM_CLICK, 0)
                            _isPreparingPlay.value = false
                        } else {
                            // Stop adding if you started something else in the meantime.
                            if (mediaPlayerHandler.queueData.value
                                    ?.data
                                    ?.playlistId != queueId
                            ) {
                                break
                            }
                            mediaPlayerHandler.loadMoreCatalog(ArrayList(batch), isAddToQueue = true)
                        }
                    }
                    if (!started) makeToast(getString(Res.string.playlist_is_empty))
                } finally {
                    _isPreparingPlay.value = false
                }
            }
    }

    fun getAlbumMore(id: String) {
        viewModelScope.launch {
            _uiState.value = MoreAlbumsUIState.Loading
            albumRepository.getAlbumMore(id, ALBUM_PARAM).collect { data ->
                if (data != null && data.second.isNotEmpty()) {
                    _uiState.value =
                        MoreAlbumsUIState.Success(
                            title = data.first,
                            albumItems = data.second,
                        )
                } else {
                    _uiState.value =
                        MoreAlbumsUIState.Error(
                            message = getString(Res.string.error),
                        )
                }
            }
        }
    }

    fun getSingleMore(id: String) {
        viewModelScope.launch {
            _uiState.value = MoreAlbumsUIState.Loading
            albumRepository.getAlbumMore(id, SINGLE_PARAM).collect { data ->
                if (data != null && data.second.isNotEmpty()) {
                    _uiState.value =
                        MoreAlbumsUIState.Success(
                            title = data.first,
                            albumItems = data.second,
                        )
                } else {
                    _uiState.value =
                        MoreAlbumsUIState.Error(
                            message = getString(Res.string.error),
                        )
                }
            }
        }
    }

    companion object {
        const val ALBUM_PARAM = "ggMIegYIARoCAQI%3D"
        const val SINGLE_PARAM = "ggMIegYIAhoCAQI%3D"
    }
}

sealed class MoreAlbumsUIState {
    data class Success(
        val title: String,
        val albumItems: List<AlbumsResult>,
    ) : MoreAlbumsUIState()

    data class Error(
        val message: String,
    ) : MoreAlbumsUIState()

    object Loading : MoreAlbumsUIState()
}