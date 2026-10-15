package com.rnfverse.rnfmusic

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.net.toUri
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eygraber.uri.toKmpUriOrNull
import com.rnfverse.common.FIRST_TIME_MIGRATION
import com.rnfverse.common.SELECTED_LANGUAGE
import com.rnfverse.common.STATUS_DONE
import com.rnfverse.common.SUPPORTED_LANGUAGE
import com.rnfverse.common.SUPPORTED_LOCATION
import com.rnfverse.domain.data.model.intent.GenericIntent
import com.rnfverse.domain.manager.DataStoreManager
import com.rnfverse.domain.mediaservice.handler.MediaPlayerHandler
import com.rnfverse.domain.mediaservice.handler.ToastType
import com.rnfverse.logger.Logger
import com.rnfverse.media3.di.setServiceActivitySession
import com.rnfverse.rnfmusic.di.viewModelModule
import com.rnfverse.rnfmusic.service.rss.RssFeedNotifyWork
import com.rnfverse.rnfmusic.service.test.notification.NotifyWork
import com.rnfverse.rnfmusic.utils.ComposeResUtils
import com.rnfverse.rnfmusic.utils.VersionManager
import com.rnfverse.rnfmusic.viewModel.SharedViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import org.rnfmusic.crashlytics.pushPlayerError
import pub.devrel.easypermissions.EasyPermissions
import java.util.Locale
import java.util.concurrent.TimeUnit

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    val viewModel: SharedViewModel by inject()
    val mediaPlayerHandler by inject<MediaPlayerHandler>()
    val dataStoreManager: DataStoreManager by inject()

    private var mBound = false
    private var shouldUnbind = false
    private val serviceConnection =
        object : ServiceConnection {
            override fun onServiceConnected(
                name: ComponentName?,
                service: IBinder?,
            ) {
//                mediaPlayerHandler.setActivitySession(this@MainActivity, MainActivity::class.java, service)
                setServiceActivitySession(this@MainActivity, MainActivity::class.java, service)
                Logger.w("MainActivity", "onServiceConnected: ")
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Logger.w("MainActivity", "onServiceDisconnected: ")
                mBound = false
            }
        }

    override fun onStart() {
        super.onStart()
        startMusicService()
    }

    override fun onStop() {
        super.onStop()
        if (shouldUnbind) {
            unbindService(serviceConnection)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Logger.d("MainActivity", "onNewIntent: $intent")
        viewModel.setIntent(
            GenericIntent(
                action = intent.action,
                data = (intent.data ?: intent.getStringExtra(Intent.EXTRA_TEXT)?.toUri())?.toKmpUriOrNull(),
                type = intent.type,
            ),
        )
    }

    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(
            module {
                single<AppCompatActivity> { this@MainActivity }
            },
        )
        // Recreate view model to fix the issue of view model not getting data from the service
        unloadKoinModules(viewModelModule)
        loadKoinModules(viewModelModule)
        VersionManager.initialize()
        checkForUpdate()
        if (viewModel.recreateActivity.value || viewModel.isServiceRunning) {
            viewModel.activityRecreateDone()
        } else {
            startMusicService()
        }
        Logger.d("MainActivity", "onCreate: ")
        val data = (intent?.data ?: intent?.getStringExtra(Intent.EXTRA_TEXT)?.toUri())?.toKmpUriOrNull()
        if (data != null) {
            viewModel.setIntent(
                GenericIntent(
                    action = intent.action,
                    data = data,
                    type = intent.type,
                ),
            )
        }
        Logger.d("Italy", "Key: ${Locale.ITALY.toLanguageTag()}")

        // Check if the migration has already been done or not
        if (getString(FIRST_TIME_MIGRATION) != STATUS_DONE) {
            Logger.d("Locale Key", "onCreate: ${Locale.getDefault().toLanguageTag()}")
            if (SUPPORTED_LANGUAGE.codes.contains(Locale.getDefault().toLanguageTag())) {
                Logger.d(
                    "Contains",
                    "onCreate: ${
                        SUPPORTED_LANGUAGE.codes.contains(
                            Locale.getDefault().toLanguageTag(),
                        )
                    }",
                )
                putString(SELECTED_LANGUAGE, Locale.getDefault().toLanguageTag())
                if (SUPPORTED_LOCATION.items.contains(Locale.getDefault().country)) {
                    putString("location", Locale.getDefault().country)
                } else {
                    putString("location", "US")
                }
            } else {
                putString(SELECTED_LANGUAGE, "en-US")
            }
            // Fetch the selected language from wherever it was stored. In this case its SharedPref
            getString(SELECTED_LANGUAGE)?.let {
                Logger.d("Locale Key", "getString: $it")
                // Set this locale using the AndroidX library that will handle the storage itself
                val localeList = LocaleListCompat.forLanguageTags(it)
                AppCompatDelegate.setApplicationLocales(localeList)
                // Set the migration flag to ensure that this is executed only once
                putString(FIRST_TIME_MIGRATION, STATUS_DONE)
            }
        }
        if (AppCompatDelegate.getApplicationLocales().toLanguageTags() !=
            getString(
                SELECTED_LANGUAGE,
            )
        ) {
            Logger.d(
                "Locale Key",
                "onCreate: ${AppCompatDelegate.getApplicationLocales().toLanguageTags()}",
            )
            putString(SELECTED_LANGUAGE, AppCompatDelegate.getApplicationLocales().toLanguageTags())
        }

        enableEdgeToEdge(
            navigationBarStyle =
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb(),
                ),
            statusBarStyle =
                SystemBarStyle.dark(
                    scrim = Color.Transparent.toArgb(),
                ),
        )
        viewModel.checkIsRestoring()
        val request =
            PeriodicWorkRequestBuilder<NotifyWork>(
                12L,
                TimeUnit.HOURS,
            ).addTag("Worker Test")
                .setConstraints(
                    Constraints
                        .Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                ).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "Artist Worker",
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
        // RnfMusic: announcements come only from Config.ANNOUNCEMENT_FEED_URL (blank = none, and any
        // job left over from before is cancelled).
        lifecycleScope.launch {
            if (com.rnfverse.common.Config.ANNOUNCEMENT_FEED_URL.isNotBlank()) {
                val rssRequest =
                    PeriodicWorkRequestBuilder<RssFeedNotifyWork>(24L, TimeUnit.HOURS)
                        .addTag("Blog RSS Worker")
                        .setConstraints(
                            Constraints
                                .Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .build(),
                        ).build()
                WorkManager.getInstance(this@MainActivity).enqueueUniquePeriodicWork(
                    "Blog RSS Worker",
                    ExistingPeriodicWorkPolicy.KEEP,
                    rssRequest,
                )
            } else {
                WorkManager.getInstance(this@MainActivity).cancelUniqueWork("Blog RSS Worker")
            }
        }
        viewModel.getLocation()

        if (!BuildConfig.DEBUG) viewModel.checkOfficialBuild(packageName, signingCertSha256())
        setContent {
            App(viewModel)
        }
    }

    override fun onDestroy() {
        val shouldStopMusicService = viewModel.shouldStopMusicService()
        Logger.w("MainActivity", "onDestroy: Should stop service $shouldStopMusicService")

        // Always unbind service if it was bound to prevent MusicBinder leak
        if (shouldStopMusicService && shouldUnbind && isFinishing) {
            viewModel.isServiceRunning = false
        }
        unloadKoinModules(viewModelModule)
        super.onDestroy()
        Logger.d("MainActivity", "onDestroy: ")
    }

    override fun onRestart() {
        super.onRestart()
        viewModel.activityRecreate()
    }

    private fun startMusicService() {
//        mediaPlayerHandler.startMediaService(this, serviceConnection)
        com.rnfverse.media3.di
            .startService(this@MainActivity, serviceConnection)
        mediaPlayerHandler.pushPlayerError = { it ->
            pushPlayerError(it)
        }
        mediaPlayerHandler.showToast = { type ->
            viewModel.makeToast(
                when (type) {
                    is ToastType.ExplicitContent -> {
                        runBlocking { ComposeResUtils.getResString(ComposeResUtils.StringType.EXPLICIT_CONTENT_BLOCKED) }
                    }

                    is ToastType.PlayerError -> {
                        runBlocking { ComposeResUtils.getResString(ComposeResUtils.StringType.TIME_OUT_ERROR, type.error) }
                    }

                    is ToastType.SponsorBlockSkip -> {
                        runBlocking { ComposeResUtils.getResString(ComposeResUtils.StringType.SPONSOR_BLOCK_SKIP, type.category) }
                    }
                },
            )
        }
        viewModel.isServiceRunning = true
        shouldUnbind = true
        Logger.d("Service", "Service started")
    }

    private fun checkForUpdate() {
        if (viewModel.shouldCheckForUpdate()) {
            viewModel.checkForUpdate()
        }
    }

    private fun putString(
        key: String,
        value: String,
    ) {
        viewModel.putString(key, value)
    }

    private fun getString(key: String): String? = viewModel.getString(key)

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        viewModel.activityRecreate()
    }
}