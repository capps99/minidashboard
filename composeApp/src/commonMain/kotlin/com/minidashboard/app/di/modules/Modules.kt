package com.minidashboard.app.di.modules

import com.minidashboard.app.Platform
import com.minidashboard.app.getPlatform
import com.minidashboard.app.presentation.home.HomeViewModel
import com.minidashboard.app.presentation.monitor.create.CreateMonitorViewModel
import com.minidashboard.app.presentation.monitor.home.MonitorViewModel
import org.koin.dsl.module

val commonModules = module {
    single<Platform> { getPlatform() }

    factory { HomeViewModel() }
    factory { MonitorViewModel() }
    factory { CreateMonitorViewModel() }
}