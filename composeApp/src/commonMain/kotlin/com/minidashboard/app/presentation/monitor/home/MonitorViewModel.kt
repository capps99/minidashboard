package com.minidashboard.app.presentation.monitor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.domain.app.CronUseCase
import com.minidashboard.app.domain.app.startProcesses
import com.minidashboard.app.domain.app.stopProcesses
import com.minidashboard.db.cron.Cron
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface MonitorState {
    data object Initial: MonitorState
    data class Data(
        val crons: List<CronProcess> = emptyList()
    ): MonitorState
}
sealed interface MonitorActions {
    data object Load: MonitorActions
    data object Start: MonitorActions
    data object Stop: MonitorActions
}

enum class Status {
    CORRECT, WARNING, ERROR
}

class MonitorViewModel(
    private val cronUseCase: CronUseCase,
) : ViewModel(){

    val state = MutableStateFlow<MonitorState>(MonitorState.Initial)

    private val cronList = listOf<CronProcess>()

    fun processAction(action: MonitorActions){
        when(action){
            MonitorActions.Load -> load()
            MonitorActions.Start -> start()
            MonitorActions.Stop -> stop()
        }
    }

    private fun load(){
        Napier.d { "start load" }
        viewModelScope.launch {
            /*val crons = cronUseCase.list().collect {
                Napier.d { "Cron inserted: " }
            }*/

            val data = MonitorState.Data(
                crons = cronUseCase.list()
            )
            state.value = data

        }
        // generateRandomStatuses()
    }

    private fun start(){
        Napier.d { "Start" }
        val state = state.value as? MonitorState.Data

        state?.let{
            startProcesses(
                state.crons
            )
        }
    }

    private fun stop(){
        Napier.d { "Stop" }
        stopProcesses()
    }

    // Function to generate a random list of statuses
    private fun generateRandomStatusList(): List<Status> {
        val randomSize = 1 //Random.nextInt(1, 4) // Generate between 1 to 3 statuses
        return List(randomSize) {
            when (Random.nextInt(3)) {
                0 -> Status.CORRECT
                1 -> Status.WARNING
                else -> Status.ERROR
            }
        }
    }

/*     private fun generateRandomStatuses() {
        // Launch a coroutine in the ViewModelScope
        viewModelScope.launch {
            while (true) {
                // Update the statuses of the list items every 2 seconds
                delay(1000)

                when(val s = state.value){
                    is MonitorState.Data -> {
                        val index = Random.nextInt(0, cronList.size)

                        cronList[index] = cronList[index]
                            .copy(
                                statuses = cronList[index].statuses + generateRandomStatusList()
                            )

                        state.value = s.copy(
                            crons = cronList,
                            attempt = s.attempt + 1
                        )
                    }
                    MonitorState.Initial -> {}
                }
            }
        }
    }  */

}