package com.minidashboard.app.domain.app.projects

import com.minidashboard.app.data.datasource.CronDataSource
import io.github.aakira.napier.Napier


class ProjectsUseCase(
    private val dataSource: CronDataSource
){

    fun list(){
        Napier.d { "Listing in usecase" }
        dataSource.list()
    }

}