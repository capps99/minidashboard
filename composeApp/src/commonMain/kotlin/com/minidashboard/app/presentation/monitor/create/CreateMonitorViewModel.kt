package com.minidashboard.app.presentation.monitor.create

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.domain.app.CronUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CreateMonitorAction {
    data object Increment: CreateMonitorAction
    data class Create(val process: CronProcess): CreateMonitorAction
}

sealed interface CreateMonitorState {
    data object Initial: CreateMonitorState
    data class Data(
        val attempt: Int = 0
    ): CreateMonitorState
}

class CreateMonitorViewModel(
    val useCase: CronUseCase
) : ViewModel() {

    val state = MutableStateFlow<CreateMonitorState>(CreateMonitorState.Initial)

    fun processAction(action: CreateMonitorAction) {
        when (action){
            CreateMonitorAction.Increment -> increment()
            is CreateMonitorAction.Create -> create(action)
        }
    }

    private fun create(action: CreateMonitorAction.Create){
        Napier.d {"Monitor with $action"}
        useCase.insert(action.process)
    }

    private fun increment(){
        Napier.d { "Increment tapped!" }
        when (val localState = state.value){
            is CreateMonitorState.Data -> {
                state.value = localState.copy(
                    attempt = localState.attempt + 1
                )
            }

            CreateMonitorState.Initial -> {
                state.value = CreateMonitorState.Data(
                    attempt = 0
                )
            }
        }
    }
}