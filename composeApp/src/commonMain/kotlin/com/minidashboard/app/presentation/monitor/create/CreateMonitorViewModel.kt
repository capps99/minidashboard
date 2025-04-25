package com.minidashboard.app.presentation.monitor.create

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.*
import com.minidashboard.app.domain.app.CronUseCase
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import com.minidashboard.app.domain.app.startProcesses
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CreateMonitorAction {
    data class Load(
        val projectUUID: String,
        val uuid: String?,
    ) : CreateMonitorAction
    data class Create(val process: Task) : CreateMonitorAction
    data class Delete(val uuid: String) : CreateMonitorAction
}

sealed interface CreateMonitorState {
    data object Initial : CreateMonitorState


    data class Data(
        // The current project.
        val project: ProjectModel?,
        // The new task.
        val task: Task?
    ) : CreateMonitorState
}

class CreateMonitorViewModel(
    private val useCase: CronUseCase,
    private val projectsUseCase: ProjectsUseCase,
) : ViewModel() {

    val state = MutableStateFlow<CreateMonitorState>(CreateMonitorState.Initial)

    fun processAction(action: CreateMonitorAction) {
        when (action) {
            is CreateMonitorAction.Load -> load(action)
            is CreateMonitorAction.Create -> create(action)
            is CreateMonitorAction.Delete -> delete(action)
        }
    }

    private fun load(action: CreateMonitorAction.Load){
        Napier.d { "with action: $action" }
        Napier.d { "Seaching data form project" }
        val project = projectsUseCase.find(uuid = action.projectUUID)

        val newState = CreateMonitorState.Data(
            project = project,
            task = null,
        )

        Napier.d { "Searching data from proccess uuid: ${action.uuid}" }
        val proccess = useCase.find(uuid = action.uuid)

        state.value = newState.copy(
            task = proccess
        )
    }

    private fun delete(action: CreateMonitorAction.Delete) {
        Napier.d { "with action: $action" }
        useCase.delete(action.uuid)
    }

    private fun create(action: CreateMonitorAction.Create) {
        Napier.d { "with action: $action" }
        val s = state.value as? CreateMonitorState.Data ?: run {
            Napier.w { "State is not Data" }
            return
        }
        s.project ?: run {
            Napier.w { "Project is not defined" }
            return
        }

        val project = s.project.copy(
            crons = s.project.crons + action.process.toCronModel()
        )

        //useCase.insert(action.process)
        projectsUseCase.save(project)
        startProcesses(
            listOf(
                action.process
            )
        )
    }
}