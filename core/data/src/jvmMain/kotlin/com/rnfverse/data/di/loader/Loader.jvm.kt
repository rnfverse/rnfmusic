package com.rnfverse.data.di.loader

import com.rnfmusic.media_jvm.di.loadDesktopPlayerModule
import com.rnfverse.data.loginsync.LoginSyncHostRepositoryImpl
import com.rnfverse.data.loginsync.LoginSyncStore
import com.rnfverse.domain.repository.LoginSyncHostRepository
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

actual fun loadMediaService() {
    loadDesktopPlayerModule()
}

actual fun loadLoginSyncModule() {
    loadKoinModules(
        module {
            single { LoginSyncStore(get(), get(), get()) }
            single<LoginSyncHostRepository> { LoginSyncHostRepositoryImpl(get()) }
        },
    )
}
