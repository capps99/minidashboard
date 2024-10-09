package com.minidashboard.app.presentation.monitor.create

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.Task
import com.minidashboard.app.domain.app.CronUseCase
import com.minidashboard.app.domain.app.startProcesses
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CreateMonitorAction {
    data class Load(
        val uuid: String?
    ) : CreateMonitorAction
    data class Create(val process: Task) : CreateMonitorAction
    data class Delete(val uuid: String) : CreateMonitorAction
}

sealed interface CreateMonitorState {
    data object Initial : CreateMonitorState
    data class Data(
        val procces: Task?
    ) : CreateMonitorState
}

class CreateMonitorViewModel(
    private val useCase: CronUseCase
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
        val uuid = action.uuid ?: run {
            state.value = CreateMonitorState.Data(
                procces = null
            )
            return
        }

        Napier.d { "Searching data from proccess uuid: $uuid" }
        val proccess = useCase.find(uuid = uuid)

        state.value = CreateMonitorState.Data(
            procces = proccess
        )
    }

    private fun delete(action: CreateMonitorAction.Delete) {
        Napier.d { "with action: $action" }
        useCase.delete(action.uuid)
    }

    private fun create(action: CreateMonitorAction.Create) {
        Napier.d { "with action: $action" }
        useCase.insert(action.process)
        startProcesses(
            listOf(
                action.process
            )
        )
    }
}