package com.minidashboard.app.di.modules

import com.minidashboard.app.Platform
import com.minidashboard.app.getPlatform
import com.minidashboard.app.presentation.home.HomeViewModel
import com.minidashboard.app.presentation.monitor.create.CreateMonitorViewModel
import com.minidashboard.app.presentation.monitor.create.command.CommandScreenViewModel
import com.minidashboard.app.presentation.monitor.create.http.HttpScreenViewModel
import com.minidashboard.app.presentation.monitor.home.MonitorViewModel
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val commonModules = module {
    single<Platform> { getPlatform() }
    single<Settings> { Settings() }

    factory { HomeViewModel() }
    factory { MonitorViewModel(get()) }
    factory { CreateMonitorViewModel(get()) }
    factory { HttpScreenViewModel() }
    factory { CommandScreenViewModel() }

}