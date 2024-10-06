package com.minidashboard.app.data.di.modules

import com.minidashboard.app.data.cache.CronDAO
import com.minidashboard.app.data.datasource.CronDataSource
import org.koin.dsl.module

val dataModules = module {
    factory { CronDAO(get()) }
    factory { CronDataSource(get(), get()) }
}