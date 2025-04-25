package com.minidashboard.app.presentation.data

import androidx.lifecycle.ViewModel
import com.minidashboard.app.domain.app.data.DataUseCase
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface DataState {
    data object Initial: DataState
    data class Data(val message: String): DataState
}

sealed interface DataActions {
    data object TapImport: DataActions
    data object TapExport: DataActions
    data object TapDelete: DataActions
}

class DataViewModel(
    private val projectsUseCase: DataUseCase,
) : ViewModel(){

    var state = MutableStateFlow(DataState.Initial)
        private set

    fun processAction(action : DataActions){
        when (action){
            DataActions.TapDelete -> clearData()
            DataActions.TapExport -> exportData()
            DataActions.TapImport -> TODO()
        }
    }

    private fun exportData() {
        TODO("Not yet implemented")
    }

    private fun clearData() {
        Napier.d { "Clearing all data" }
        projectsUseCase.clear()
    }

}