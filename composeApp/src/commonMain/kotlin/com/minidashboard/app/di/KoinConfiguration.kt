package com.minidashboard.app.di

import com.minidashboard.app.di.modules.commonModules
import com.minidashboard.app.domain.di.modules.domainModules
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.koinApplication

fun koinConfiguration(additionalModules: List<Module> = emptyList()) = koinApplication {
    modules(additionalModules + platformModule())
    modules(
        commonModules,
        domainModules,
    )
}

fun initKoin(additionalModules: List<Module> = emptyList()) {
    Napier.base(DebugAntilog())
    startKoin(
        koinConfiguration(
            additionalModules
        )
    )
}

expect fun platformModule(): Module