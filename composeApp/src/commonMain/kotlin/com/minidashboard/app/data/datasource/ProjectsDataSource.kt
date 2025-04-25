package com.minidashboard.app.data.datasource

import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.domain.persistence.DriverFactory
import com.russhwolf.settings.Settings

expect class ProjectsDataSource(
    driverFactory: DriverFactory,
    settings: Settings,
) {

    fun insert(data: ProjectModel)
    fun update(data: ProjectModel)
    fun list(): List<ProjectModel>
    fun delete()
    fun find(uuid: String): ProjectModel?

    fun importData()
    fun exportData()
    fun clearAllData()

}