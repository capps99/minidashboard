package com.minidashboard.app.domain.persistence

import app.cash.sqldelight.db.SqlDriver

import com.minidashboard.db.Database

const val DB_NAME = "app-database.db"
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun storage(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()
    val database = Database(driver)

    // Do more work with the database (see below).

    return database
}