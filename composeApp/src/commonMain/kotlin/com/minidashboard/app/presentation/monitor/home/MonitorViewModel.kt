package com.minidashboard.app.presentation.monitor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.domain.app.CronUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed interface MonitorState {
    data object Initial: MonitorState
    data class Data(
        val attempt: Int = 0,
        val crons: List<CronProcess> = emptyList()
    ): MonitorState
}
sealed interface MonitorActions {
    data object Load: MonitorActions
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
        }
    }

    private fun load(){
        Napier.d { "start load" }
        val crons = cronUseCase.list()
        val data = MonitorState.Data(
            attempt = crons.size,
            crons = cronList,
        )
        state.value = data

        // generateRandomStatuses()
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