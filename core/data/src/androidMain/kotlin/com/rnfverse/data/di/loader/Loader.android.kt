package com.rnfverse.data.di.loader

import com.rnfverse.media3.di.loadMediaService
import com.rnfverse.data.loginsync.LoginSyncSenderRepositoryImpl
import com.rnfverse.data.loginsync.LoginSyncStore
import com.rnfverse.domain.repository.LoginSyncSenderRepository
import org.koin.core.context.loadKoinModules
import org.rnfmusic.loginsync.LoginSyncClient
import org.koin.dsl.module

actual fun loadMediaService() {
    loadMediaService()
}

actual fun loadLoginSyncModule() {
    loadKoinModules(
        module {
            single { LoginSyncStore(get(), get(), get()) }
            single<LoginSyncSenderRepository> { LoginSyncSenderRepositoryImpl(get(), LoginSyncClient()) }
        },
    )
}
