package com.minidashboard.app.data.datasource

import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings
import io.github.aakira.napier.Napier

actual class ProjectsDataSource actual constructor(
    driverFactory: DriverFactory,
    settings: Settings
) {
    object companion {
        const val KEY = "PROJECTS"
    }

    actual fun insert() {
        Napier.d { "JVM - insert" }
    }

    actual fun list() {
        Napier.d { "JVM - list" }
    }

    actual fun delete() {
        Napier.d { "JVM - delete" }
    }

    actual fun find(uuid: String) {
        Napier.d { "JVM - find" }
    }

}