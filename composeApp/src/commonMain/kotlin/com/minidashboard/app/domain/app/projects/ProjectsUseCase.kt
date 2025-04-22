package com.minidashboard.app.domain.app.projects

import com.minidashboard.app.data.datasource.CronDataSource
import com.minidashboard.app.data.models.ProjectModel
import io.github.aakira.napier.Napier


class ProjectsUseCase(
    private val dataSource: CronDataSource
){

    fun list(): List<ProjectModel> {
        Napier.d { "Listing in usecase" }
        return dataSource.list()
    }

}