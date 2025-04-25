package com.minidashboard.app.domain.app.projects

import com.minidashboard.app.data.datasource.CronDataSource
import com.minidashboard.app.data.datasource.ProjectsDataSource
import com.minidashboard.app.data.models.ProjectModel
import io.github.aakira.napier.Napier


class ProjectsUseCase(
    private val dataSource: ProjectsDataSource
){

    fun find(uuid: String): ProjectModel? {
        Napier.d { "Finding [PROJECT] in usecase" }
        return dataSource.find(uuid = uuid)
    }

    fun list(): List<ProjectModel> {
        Napier.d { "Listing [PROJECT] in usecase" }
        return dataSource.list()
    }

    fun save(data: ProjectModel){
        Napier.d { "Save [PROJECT] in usecase" }
        dataSource.update(data)
    }

}