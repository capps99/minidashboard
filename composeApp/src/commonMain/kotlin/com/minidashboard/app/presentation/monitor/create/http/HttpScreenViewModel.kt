package com.minidashboard.app.presentation.monitor.create.http

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.*
import com.minidashboard.app.domain.app.runSingleProcess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface TestMonitorAction {
    data class Test(
        val process: CronProcess
    ) : TestMonitorAction
    data object hideError:TestMonitorAction
}

sealed interface HttpScreenState {
    data object Initial : HttpScreenState
    data object Loading : HttpScreenState
    data class Data(
        val result: ProccessResult,
        val isValid: Boolean = true,
    ) : HttpScreenState

    data class Messages(
        val title: String,
        val message: String,
    ) : HttpScreenState
}

class HttpScreenViewModel : ViewModel() {

    val state = MutableStateFlow<HttpScreenState>(HttpScreenState.Initial)

    fun processAction(action: TestMonitorAction) {
        when (action) {
            is TestMonitorAction.Test -> test(action)
            TestMonitorAction.hideError -> hideError()
        }
    }

    private fun test(action: TestMonitorAction.Test) {
        Napier.d { "Monitor test with $action" }
        state.value = HttpScreenState.Loading

        Napier.d { action.process.toJson() }
        runSingleProcess(action.process) { (result, valid) ->
            state.value = when (valid) {
                true -> HttpScreenState.Data(
                    result = result,
                    isValid = valid,
                )

                false -> HttpScreenState.Messages(
                    title = "Error",
                    message = "Test failed to comply"
                )
            }

        }
    }

    private fun hideError(){
        state.value = HttpScreenState.Initial
    }

    fun createRules(
        httpMatchCode: String?
    ): List<Rule> {
        val rules = mutableListOf<Rule>()

        httpMatchCode?.toIntOrNull()?.let {
            rules.add(
                MatchIntRule(
                    expected = it,
                )
            )
        }

        return rules
    }

}