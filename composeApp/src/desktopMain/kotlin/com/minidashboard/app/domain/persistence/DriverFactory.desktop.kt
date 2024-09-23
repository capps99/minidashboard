package com.minidashboard.app.domain.persistence

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.minidashboard.db.Database
import io.github.aakira.napier.Napier

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        //val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:${DB_NAME}")

        Fix this when launched first time
        if(Database.Schema.version == 0L){
            Napier.d { "creating table" }
            Database.Schema.create(driver)
        }

        return driver
    }
}