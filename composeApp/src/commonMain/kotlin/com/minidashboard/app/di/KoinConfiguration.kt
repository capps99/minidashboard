package com.minidashboard.app.di

import com.minidashboard.app.di.modules.commonModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.dsl.koinApplication

fun koinConfiguration() = koinApplication {
    modules(
        commonModules,
    )
}

fun initKoin() {
    Napier.base(DebugAntilog())
    startKoin(koinConfiguration())
}