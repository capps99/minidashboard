package com.minidashboard.app.presentation.monitor.create.http

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.ProccessResult
import com.minidashboard.app.data.models.SetupConfig
import com.minidashboard.app.data.models.toJson
import com.minidashboard.app.domain.app.runSingleProcess
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow

sealed interface TestMonitorAction {
    data class Test(
        val process: CronProcess
    ) : TestMonitorAction
}

sealed interface HttpScreenState {
    data object Initial : HttpScreenState
    data object Loading : HttpScreenState
    data class Data(
        val result: ProccessResult
    ) : HttpScreenState
}


class HttpScreenViewModel : ViewModel(){

    val state = MutableStateFlow<HttpScreenState>(HttpScreenState.Initial)

    fun processAction(action: TestMonitorAction) {
        when (action) {
            is TestMonitorAction.Test -> test(action)
        }
    }

    private fun test(action: TestMonitorAction.Test) {
        Napier.d { "Monitor test with $action" }
        state.value = HttpScreenState.Loading

        Napier.d { action.process.toJson() }
        runSingleProcess(action.process){ result ->
            state.value = HttpScreenState.Data(result)
        }

    }

}