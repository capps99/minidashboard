package com.minidashboard.app.di

import com.minidashboard.app.domain.persistence.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single { DriverFactory() }
    }
}