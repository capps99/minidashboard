package com.minidashboard.app.domain.app.data

import com.minidashboard.app.data.datasource.ProjectsDataSource
import io.github.aakira.napier.Napier

class DataUseCase(
    private val dataSource: ProjectsDataSource
) {

    fun import(){
        dataSource.importData()
    }

    fun export(){
        dataSource.exportData()
    }

    fun clear(){
        Napier.d { "Clearing all storage data" }
        dataSource.clearAllData()
    }

}