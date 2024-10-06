package com.minidashboard.app.data.datasource

import com.minidashboard.app.data.models.CronModel
import com.minidashboard.app.domain.persistence.DriverFactory
import com.minidashboard.db.cron.Cron
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier

actual class CronDataSource actual constructor(
    driverFactory: DriverFactory,
    settings: Settings
) {
    actual fun insert(uuid: String, setup: String, active: Boolean) {
        Napier.d { "Android - insert" }
    }

    actual fun list(): List<CronModel> {
        Napier.d { "Android - list" }
        return emptyList()
    }

}