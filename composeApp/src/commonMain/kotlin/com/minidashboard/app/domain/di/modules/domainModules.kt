package com.minidashboard.app.domain.di.modules

import com.minidashboard.app.domain.app.CronUseCase
import com.minidashboard.app.domain.cache.CronDAO
import org.koin.dsl.module

val domainModules = module {
    factory { CronUseCase(get())  }
    factory { CronDAO(get()) }
}