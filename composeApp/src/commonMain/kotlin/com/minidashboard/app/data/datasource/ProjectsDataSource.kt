package com.minidashboard.app.data.datasource

import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings

expect class ProjectsDataSource(
    driverFactory: DriverFactory,
    settings: Settings,
) {

    fun insert()
    fun list()
    fun delete()
    fun find(uuid: String)

}