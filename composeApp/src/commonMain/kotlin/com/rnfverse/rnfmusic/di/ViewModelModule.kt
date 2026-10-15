package com.rnfverse.rnfmusic.di

import com.rnfverse.rnfmusic.viewModel.AlbumViewModel
import com.rnfverse.rnfmusic.utils.VersionManager
import com.rnfverse.rnfmusic.viewModel.AnalyticsViewModel
import com.rnfverse.rnfmusic.viewModel.ListenTogetherSettingsViewModel
import com.rnfverse.rnfmusic.viewModel.ListenTogetherViewModel
import com.rnfverse.rnfmusic.viewModel.ArtistViewModel
import com.rnfverse.rnfmusic.viewModel.BrowseViewModel
import com.rnfverse.rnfmusic.viewModel.HomeViewModel
import com.rnfverse.rnfmusic.viewModel.ImportViewModel
import com.rnfverse.rnfmusic.viewModel.LibraryDynamicPlaylistViewModel
import com.rnfverse.rnfmusic.viewModel.LibraryViewModel
import com.rnfverse.rnfmusic.viewModel.LocalPlaylistViewModel
import com.rnfverse.rnfmusic.viewModel.LogInViewModel
import com.rnfverse.rnfmusic.viewModel.LoginSyncHostViewModel
import com.rnfverse.rnfmusic.viewModel.LoginSyncSenderViewModel
import com.rnfverse.rnfmusic.viewModel.MoodViewModel
import com.rnfverse.rnfmusic.viewModel.MoreAlbumsViewModel
import com.rnfverse.rnfmusic.viewModel.NotificationViewModel
import com.rnfverse.rnfmusic.viewModel.NowPlayingBottomSheetViewModel
import com.rnfverse.rnfmusic.viewModel.PlaylistViewModel
import com.rnfverse.rnfmusic.viewModel.PodcastViewModel
import com.rnfverse.rnfmusic.viewModel.RecentlySongsViewModel
import com.rnfverse.rnfmusic.viewModel.SearchViewModel
import com.rnfverse.rnfmusic.viewModel.AutoEqViewModel
import com.rnfverse.rnfmusic.viewModel.SettingsViewModel
import com.rnfverse.rnfmusic.viewModel.SharedViewModel
import com.rnfverse.rnfmusic.viewModel.SongSelectionViewModel
import com.rnfverse.rnfmusic.viewModel.TasteViewModel
import com.rnfverse.rnfmusic.viewModel.WrappedViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        single {
            SharedViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        single {
            SearchViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            SongSelectionViewModel(
                get(),
                get(),
            )
        }
        // Only one of these resolves on a given platform: Desktop binds the host repository, Android the
        // sender. Koin resolves dependencies when a ViewModel is created, not when it is declared.
        viewModel { LoginSyncHostViewModel(get()) }
        viewModel { LoginSyncSenderViewModel(get()) }
        viewModel {
            NowPlayingBottomSheetViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LibraryDynamicPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ImportViewModel(
                get(),
            )
        }
        viewModel {
            AlbumViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            HomeViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AutoEqViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            SettingsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ArtistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            PlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            LogInViewModel(
                get(),
            )
        }
        viewModel {
            PodcastViewModel(
                get(),
            )
        }
        viewModel {
            MoreAlbumsViewModel(
                get(),
            )
        }
        viewModel {
            BrowseViewModel(
                get(),
            )
        }
        viewModel {
            RecentlySongsViewModel(
                get(),
            )
        }
        viewModel {
            LocalPlaylistViewModel(
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            NotificationViewModel(
                get(),
            )
        }
        viewModel {
            MoodViewModel(
                get(),
                get(),
            )
        }
        viewModel {
            AnalyticsViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            WrappedViewModel(
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            TasteViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        viewModel {
            ListenTogetherSettingsViewModel(get())
        }
        viewModel {
            ListenTogetherViewModel(
                repository = get(),
                dataStore = get(),
                bridge = get(),
            )
        }

    }