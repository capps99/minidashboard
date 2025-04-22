package com.minidashboard.app.data.datasource

import com.minidashboard.app.data.models.CronModel
import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.domain.persistence.DriverFactory
import com.minidashboard.db.cron.Cron
import com.russhwolf.settings.Settings

expect class CronDataSource(
    driverFactory: DriverFactory,
    settings: Settings,
    ) {
    fun insert(
        uuid: String,
        setup: String,
        active: Boolean
    )

    fun list(): List<ProjectModel>

    fun delete(
        uuid: String,
    )
}