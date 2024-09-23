package com.minidashboard.app.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.minidashboard.db.Database
import com.minidashboard.app.domain.persistence.DB_NAME
import com.minidashboard.app.domain.persistence.DriverFactory
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module {
    return module {
        single<SqlDriver> {
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = get(),
                name = DB_NAME
            )
        }
        single { DriverFactory(get()) }
    }
}