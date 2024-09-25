package com.minidashboard.app.domain.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.minidashboard.db.Database
import io.github.aakira.napier.Napier
import org.koin.core.component.getScopeName
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        //val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${DB_NAME}")

        val exist = File(DB_NAME).exists()
        Napier.d{"db exist = $exist"}

        if(!File(DB_NAME).exists()){
            // Create schema only once.
            Napier.d { "creating table" }
            Database.Schema.create(driver)
        }

        return driver
    }
}