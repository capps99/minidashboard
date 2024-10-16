package com.minidashboard.app.presentation.projects

import androidx.lifecycle.ViewModel
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import kotlinx.coroutines.flow.MutableStateFlow


sealed interface ProjectsState {
    data object Initial: ProjectsState
    data class Data(
        val projects: List<String>
    ) :ProjectsState
}

sealed interface ProjectsActions {
    data object Load: ProjectsActions
    data object Create: ProjectsActions
}

class ProjectsViewModel(
    private val useCase: ProjectsUseCase
) : ViewModel() {
    var state: MutableStateFlow<ProjectsState> = MutableStateFlow(ProjectsState.Initial)
        private set

    fun processAction(action: ProjectsActions){
        when(action){
            ProjectsActions.Create -> TODO()
            ProjectsActions.Load -> load()
        }
    }

    private fun load(){
        useCase.list()
        val items = listOf(
            "Palbarapp",
            "RTCDMX",
            "Funflags",
            "+ Crear",
        )

        state.value = ProjectsState.Data(
            projects = items
        )
    }


}