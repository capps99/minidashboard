package com.minidashboard.app.presentation.projects.create

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface ProjectCreateState {
    data object Initial: ProjectCreateState
    data class State(
        val project: String,
        val description: String,
    ): ProjectCreateState
}
sealed interface ProjectCreateActions {
    data object Create: ProjectCreateActions
}

class ProjectCreateViewModel: ViewModel() {
    var state: MutableStateFlow<ProjectCreateState> = MutableStateFlow(ProjectCreateState.Initial)
        private set

    fun proccesAction(action: ProjectCreateActions){
        when (action){
            ProjectCreateActions.Create -> TODO()
        }
    }

}