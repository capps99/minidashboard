package com.minidashboard.app.data.datasource

import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings

actual class ProjectsDataSource actual constructor(
    driverFactory: DriverFactory,
    settings: Settings
) {
    actual fun insert() {
    }

    actual fun list() {
    }

    actual fun delete() {
    }

    actual fun find(uuid: String) {
    }

}