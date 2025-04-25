package com.minidashboard.app.presentation.projects.create

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.ProjectModel
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import com.minidashboard.app.presentation.projects.home.ProjectsActions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.random.Random

sealed interface ProjectCreateState {
    data class State(
        val project: String,
        val description: String,
    ): ProjectCreateState
}
sealed interface ProjectCreateActions {
    data object Save: ProjectCreateActions
    data object Reset: ProjectCreateActions
}

class ProjectCreateViewModel(
    private val projectsUseCase: ProjectsUseCase,
): ViewModel() {
    var state: MutableStateFlow<ProjectCreateState> = MutableStateFlow(
        ProjectCreateState.State(project = "", description = "")
    )
        private set

    fun proccesAction(action: ProjectCreateActions){
        when (action){
            ProjectCreateActions.Save -> save()
            ProjectCreateActions.Reset -> reset()
        }
    }

    private fun reset() {
        val current = state.value as? ProjectCreateState.State ?: return
        state.value = current.copy(
            project = "",
            description = "",
        )
    }

    private fun save() {
        val current = state.value as? ProjectCreateState.State ?: return

        projectsUseCase.save(
            ProjectModel(
                uuid =  Random.nextInt(1, 999_999).toString(),
                name = current.project,
                description = current.description,
                enabled = true,
            )
        )
    }

    fun updateTextFieldProject(value: String){
        val current = state.value as? ProjectCreateState.State ?: return
        state.value = current.copy(
            project = value
        )
    }

    fun updateTextFieldDescription(value: String){
        val current = state.value as? ProjectCreateState.State ?: return
        state.value = current.copy(
            description = value
        )
    }

}