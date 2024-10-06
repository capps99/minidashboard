package com.minidashboard.app.data.datasource

import com.minidashboard.app.domain.persistence.DriverFactory
import com.minidashboard.db.cron.Cron
import com.russhwolf.settings.Settings

actual class CronDataSource actual constructor(
    driverFactory: DriverFactory,
    settings: Settings
) {
    actual fun insert(uuid: String, setup: String, active: Boolean) {
    }

    actual fun list(): List<Cron> {
        TODO("Not yet implemented")
    }

}