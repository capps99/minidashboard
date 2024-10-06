package com.minidashboard.app.domain.di.modules

import com.minidashboard.app.domain.app.CronUseCase
import org.koin.dsl.module

val domainModules = module {
    factory { CronUseCase(get())  }
}