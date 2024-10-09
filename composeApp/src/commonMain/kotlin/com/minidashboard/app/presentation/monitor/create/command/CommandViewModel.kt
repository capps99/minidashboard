package com.minidashboard.app.presentation.monitor.create.command

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.Task
import com.minidashboard.app.data.models.MatchIntRule
import com.minidashboard.app.data.models.ProccessResult
import com.minidashboard.app.data.models.Rule
import com.minidashboard.app.domain.app.runSingleProcess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface CommandMonitorAction {
    data class Test(
        val process: Task
    ) : CommandMonitorAction
    data object hideError:CommandMonitorAction
}

sealed interface CommandScreenState {
    data object Initial : CommandScreenState
    data object Loading : CommandScreenState
    data class Data(
        val result: ProccessResult,
        val isValid: Boolean = true,
    ) : CommandScreenState

    data class Messages(
        val title: String,
        val message: String,
    ) : CommandScreenState
}

class CommandScreenViewModel : ViewModel() {

    val state = MutableStateFlow<CommandScreenState>(CommandScreenState.Initial)

    fun processAction(action: CommandMonitorAction) {
        when (action) {
            is CommandMonitorAction.Test -> test(action)
            CommandMonitorAction.hideError -> hideError()
        }
    }

    private fun test(action: CommandMonitorAction.Test) {
        Napier.d { "Command Test with $action" }
        state.value = CommandScreenState.Loading

        // Napier.d { action.process.toJson() }
        runSingleProcess(action.process) { (result, valid) ->
            state.value = when (valid) {
                true -> CommandScreenState.Data(
                    result = result,
                    isValid = valid,
                )

                false -> CommandScreenState.Messages(
                    title = "Error",
                    message = "Test failed to comply"
                )
            }

        }
    }

    private fun hideError(){
        state.value = CommandScreenState.Initial
    }

    fun createRules(
        ruleExitCode: String?
    ): List<Rule> {
        val rules = mutableListOf<Rule>()

        ruleExitCode?.toIntOrNull()?.let {
            rules.add(
                MatchIntRule(
                    expected = it,
                )
            )
        }

        return rules
    }

}