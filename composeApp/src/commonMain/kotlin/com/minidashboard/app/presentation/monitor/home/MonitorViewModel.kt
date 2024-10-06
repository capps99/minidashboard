package com.minidashboard.app.presentation.monitor.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minidashboard.app.data.models.CommandResult
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.HttpResult
import com.minidashboard.app.data.models.ProccessResult
import com.minidashboard.app.domain.app.*
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


data class CronItem(
    val cron: CronProcess,
    val statuses: List<Status>
)

sealed interface MonitorState {
    data object Initial : MonitorState
    data class Data(
        val crons: Map<String, CronItem> = emptyMap(),
        val isProcessRuning: Boolean = false,
    ) : MonitorState
}

sealed interface MonitorActions {
    data object Load : MonitorActions
    data object Start : MonitorActions
    data object Stop : MonitorActions
    data class Edit(
        val cronItem: CronItem
    ) : MonitorActions
}

enum class Status {
    CORRECT, WARNING, ERROR
}

class MonitorViewModel(
    private val cronUseCase: CronUseCase,
) : ViewModel() {

    val state = MutableStateFlow<MonitorState>(MonitorState.Initial)

    private val mutex = Mutex()

    fun processAction(action: MonitorActions) {
        when (action) {
            MonitorActions.Load -> load()
            MonitorActions.Start -> start()
            MonitorActions.Stop -> stop()
            is MonitorActions.Edit -> edit(action.cronItem)
        }
    }

    private fun load() {
        Napier.d { "start load" }
        viewModelScope.launch {
            /*val crons = cronUseCase.list().collect {
                Napier.d { "Cron inserted: " }
            }*/

            val data = MonitorState.Data(
                crons = cronUseCase.list().map { cron ->
                    cron.common.uuid to CronItem(
                        cron = cron,
                        statuses = emptyList(),
                    )
                }.toMap(),
                isProcessRuning = isProcessesRunning(),
            )

            state.value = data
        }
        // generateRandomStatuses()
    }

    private fun edit(cronItem: CronItem){
        
    }

    private fun start() {
        Napier.d { "Start" }
        val state = state.value as? MonitorState.Data

        state?.let {
            val list = state.crons.values.map { it.cron }
            startProcesses(list) {
                viewModelScope.launch {
                    collect(it)
                }
            }
        }
    }

    private fun stop() {
        Napier.d { "Stop" }
        stopProcesses()

        val state = state.value as? MonitorState.Data ?: return
        this.state.value = state.copy(
            isProcessRuning = false,
        )
    }

    private suspend fun collect(result: Result) {
        mutex.withLock {
            val (processResult, result) = result
            Napier.d { "Collecting new procces result" }
            Napier.d { "Procces: $processResult with result: $result" }

            val state = state.value
            when (state) {
                is MonitorState.Data -> {
                    val data = updateDataSource(
                        input = state.crons,
                        process = processResult,
                    )

                    Napier.d { "Updating state" }
                    this.state.value = state.copy(
                        crons = data,
                        isProcessRuning = true,
                    )
                }

                MonitorState.Initial -> return
            }
        }
    }

    private fun updateDataSource(input: Map<String, CronItem>, process: ProccessResult): MutableMap<String, CronItem> {
        val resp = input.toMutableMap()
        when (val process = process) {
            // Fix this and comprees in a better emthod
            is HttpResult -> {
                val cache = resp[process.uuid] ?: run {
                    Napier.d { "Cron data not found with id: ${process.uuid}" }
                    return resp
                }

                resp[process.uuid] = cache.copy(
                    statuses = (cache.statuses + when (process.proccess.validate()) {
                        true -> Status.CORRECT
                        false -> Status.ERROR
                    }).take(10)
                )
            }
            is CommandResult -> {
                val cache = resp[process.uuid] ?: run {
                    Napier.d { "Cron data not found with id: ${process.uuid}" }
                    return resp
                }

                resp[process.uuid] = cache.copy(
                    statuses = (cache.statuses + when (process.proccess.validate()) {
                        true -> Status.CORRECT
                        false -> Status.ERROR
                    }).take(10)
                )
            }
        }

        return resp
    }
}