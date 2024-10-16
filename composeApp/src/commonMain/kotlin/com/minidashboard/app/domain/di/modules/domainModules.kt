package com.minidashboard.app.domain.di.modules

import com.minidashboard.app.domain.app.CronUseCase
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import org.koin.dsl.module

val domainModules = module {
    factory { CronUseCase(get())  }
    factory { ProjectsUseCase(get()) }
}