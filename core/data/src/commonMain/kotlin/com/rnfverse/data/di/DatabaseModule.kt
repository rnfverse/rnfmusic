package com.rnfverse.data.di

import DatabaseDao
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rnfverse.data.dataStore.DataStoreManagerImpl
import com.rnfverse.data.dataStore.createDataStoreInstance
import com.rnfverse.data.db.Converters
import com.rnfverse.data.db.MusicDatabase
import com.rnfverse.data.db.datasource.AnalyticsDatasource
import com.rnfverse.data.db.datasource.LocalDataSource
import com.rnfverse.data.db.getDatabaseBuilder
import com.rnfverse.domain.manager.DataStoreManager
import com.rnfverse.kotlinytmusicscraper.YouTube
import com.rnfverse.spotify.Spotify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import org.rnfmusic.aiservice.AiClient
import org.rnfmusic.lyrics.RnfMusicLyricsClient
import kotlin.time.ExperimentalTime
import org.rnfmusic.autoeq.AutoEq

@OptIn(ExperimentalTime::class)
val databaseModule =
    module {
        single(createdAtStart = true) {
            Converters()
        }
        // Database
        single(createdAtStart = true) {
            getDatabaseBuilder(
                get<Converters>()
            )
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
        }
        // DatabaseDao
        single(createdAtStart = true) {
            get<MusicDatabase>().getDatabaseDao()
        }
        // LocalDataSource
        single(createdAtStart = true) {
            LocalDataSource(get<DatabaseDao>(), get<MusicDatabase>())
        }
        // AnalyticsDatasource
        single(createdAtStart = true) {
            AnalyticsDatasource(get<DatabaseDao>())
        }
        // Datastore
        single(createdAtStart = true) {
            createDataStoreInstance()
        }
        // DatastoreManager
        single<DataStoreManager>(createdAtStart = true) {
            DataStoreManagerImpl(get<DataStore<Preferences>>())
        }

        // Move YouTube from Singleton to Koin DI
        single(createdAtStart = true) {
            YouTube()
        }

        single(createdAtStart = true) {
            Spotify()
        }

        single(createdAtStart = true) {
            AiClient()
        }

        single(createdAtStart = true) {
            RnfMusicLyricsClient()
        }

        // Not created at start, unlike the rest: nothing needs it until someone opens the AutoEq
        // picker, and it holds an HTTP client the vast majority of sessions never use.
        single {
            AutoEq()
        }
    }