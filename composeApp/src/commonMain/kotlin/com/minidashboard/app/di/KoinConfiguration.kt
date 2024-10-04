package com.minidashboard.app.di

import com.minidashboard.app.di.modules.commonModules
import com.minidashboard.app.domain.app.FileLogger
import com.minidashboard.app.domain.di.modules.domainModules
import com.minidashboard.app.getPlatform
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

    val platform = getPlatform().name
    println("platform: $platform")
    if(platform.contains("java", ignoreCase = true)){
        println("File Logger: $platform")
        Napier.base(FileLogger())
    }
    else {
        println("Standart Logger: $platform")
    }
    
    Napier.base(DebugAntilog())
    startKoin(
        koinConfiguration(
            additionalModules
        )
    )
}

expect fun platformModule(): Module