package com.minidashboard.app.domain.persistence

import com.minidashboard.db.Database
import app.cash.sqldelight.driver.native.NativeSqliteDriver

actual class DriverFactory {
    actual fun createDriver(): app.cash.sqldelight.db.SqlDriver {
        return NativeSqliteDriver(Database.Schema, DB_NAME)
    }
}