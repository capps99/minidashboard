package com.minidashboard.app.presentation.monitor.create

import androidx.lifecycle.ViewModel
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.domain.app.Process
import com.minidashboard.app.domain.app.runSingleProcess
import io.github.aakira.napier.Napier

sealed interface TestMonitorAction {
    data class Test(
        val process: CronProcess
    ) : TestMonitorAction
}

sealed interface TestMonitorState {
    data object Initial : TestMonitorState
    data class Data(
        val attempt: Int = 0
    ) : TestMonitorState
}


class TestMonitorViewModel : ViewModel(){

    fun processAction(action: TestMonitorAction) {
        when (action) {
            is TestMonitorAction.Test -> test(action)
        }
    }

    private fun test(action: TestMonitorAction.Test) {
        Napier.d { "Monitor test with $action" }
        runSingleProcess(action.process)
    }

}