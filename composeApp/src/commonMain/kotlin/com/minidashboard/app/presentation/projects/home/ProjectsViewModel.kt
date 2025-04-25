package com.minidashboard.app.presentation.projects.home

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow


sealed interface ProjectsState {
    data object Initial: ProjectsState
    data class Data(
        val projects: List<ProjectModel>
    ) : ProjectsState
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
        val data = useCase.list() + ProjectModel(
            uuid = "new",
            name = "+ Create",
            description = "",
            enabled = true
        )
        Napier.d { "Projects list: $data" }
        state.value = ProjectsState.Data(
            projects = data
        )
    }


}