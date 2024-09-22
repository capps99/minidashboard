package com.minidashboard.app.presentation.monitor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface MonitorState {
    data object Initial: MonitorState
    data class Data(
        val attempt: Int = 0,
        val crons: List<CronItem> = emptyList()
    ): MonitorState
}
sealed interface MonitorActions {
    data object Load: MonitorActions
}

data class CronItem(
    val title: String,
    val description: String,
    val statuses: List<Status> = emptyList()// Multiple statuses
)

enum class Status {
    CORRECT, WARNING, ERROR
}

class MonitorViewModel : ViewModel(){

    val state = MutableStateFlow<MonitorState>(MonitorState.Initial)

    private val cronList = mutableListOf(
        CronItem("cron 1","description 1"),
        CronItem("cron 2","description 2"),
        CronItem("cron 3","description 3"),
        CronItem("cron 4","description 4"),
        CronItem("cron 5","description 5"),
        CronItem("cron 6","description 6"),
        CronItem("cron 7","description 7"),
        CronItem("cron 8","description 8"),
    )

    fun processAction(action: MonitorActions){
        when(action){
            MonitorActions.Load -> load()
        }
    }

    private fun load(){
        Napier.d { "start load" }
        val data = MonitorState.Data(
            crons = cronList,
        )
        state.value = data

        generateRandomStatuses()
    }

    private fun create(){
        Napier.d { "Launch create note" }
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

    private fun generateRandomStatuses() {
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
    }

}