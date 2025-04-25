package com.minidashboard.app.presentation.monitor.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minidashboard.app.data.models.*
import com.minidashboard.app.domain.app.*
import com.minidashboard.app.domain.app.projects.ProjectsUseCase
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

sealed interface MonitorState {
    data object Initial : MonitorState
    data class Data(
        val project: ProjectModel,
        val crons: Map<String, CronItem> = emptyMap(),
        val isProcessRuning: Boolean = false,
    ) : MonitorState
}

sealed interface MonitorActions {
    data class Load(
        val projectUUID: String,
    ) : MonitorActions
    data object Start : MonitorActions
    data object Stop : MonitorActions
    data class Edit(
        val cronItem: CronItem
    ) : MonitorActions
}

class MonitorViewModel(
    private val projectsUseCase: ProjectsUseCase,
    private val cronUseCase: CronUseCase,
) : ViewModel() {

    val state = MutableStateFlow<MonitorState>(MonitorState.Initial)

    private val mutex = Mutex()

    fun processAction(action: MonitorActions) {
        when (action) {
            is MonitorActions.Load -> load(action)
            MonitorActions.Start -> start()
            MonitorActions.Stop -> stop()
            is MonitorActions.Edit -> edit(action.cronItem)
        }
    }

    private fun load(action: MonitorActions.Load) {
        Napier.d { "start load" }
        viewModelScope.launch {
            /*val crons = cronUseCase.list().collect {
                Napier.d { "Cron inserted: " }
            }*/
            val project = projectsUseCase.find(action.projectUUID)?: run {
                Napier.w { "Can't fin project by ID" }
                return@launch
            }

            val data = MonitorState.Data(
                project = project,
                crons = project.crons.map { cron ->
                    val uuid = cron.uuid ?: "undefined"
                    val task = cron.toTask()
                    task?.let {
                        uuid to CronItem(
                            task = task,
                            statuses = emptyList()
                        )
                    }
                }.filterNotNull().toMap(),
                isProcessRuning = isProcessesRunning(),
            )

            state.value = data
        }
        // generateRandomStatuses()
    }

    private fun edit(cronItem: CronItem) {

    }

    private fun start() {

        Napier.d { "Start" }
        val state = state.value as? MonitorState.Data

        state?.let {
            this.state.value = state.copy(
                isProcessRuning = true,
            )
            
            val list = state.crons.values.map { it.task }
            startProcesses(
                list,
                onLaunched = {
                    viewModelScope.launch {
                        collect(it)
                    }
                },
                onResult = {
                    viewModelScope.launch {
                        collect(it)
                    }
                }
            )
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

    private suspend fun collect(task: Task) {
        mutex.withLock {
            Napier.d { "Collecting new task launched" }

            val state = state.value
            when (state) {
                is MonitorState.Data -> {
                    val task = jobs[task.uuid] ?: return
                    val crons = state.crons.toMutableMap()
                    crons[task.task.uuid] = task.copy(
                        // Limit status on last 50
                        statuses = task.statuses.takeLast(50)
                    )

                    Napier.d { "Updating state" }
                    this.state.value = state.copy(
                        crons = crons
                    )
                }

                MonitorState.Initial -> return
            }
        }
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
                        crons = data
                    )
                }

                MonitorState.Initial -> return
            }
        }
    }

    private fun updateDataSource(input: Map<String, CronItem>, process: ProccessResult): MutableMap<String, CronItem> {
        val resp = input.toMutableMap()
        val jobs = jobs
        when (val process = process) {
            // Fix this and comprees in a better emthod
            is HttpResult -> {
                val cache = resp[process.uuid] ?: run {
                    Napier.d { "Cron data not found with id: ${process.uuid}" }
                    return resp
                }

                jobs[process.uuid]?.let {
                    resp[process.uuid] = it.copy(
                        statuses = it.statuses.takeLast(50)
                    )
                }
            }

            is CommandResult -> {
                val cache = resp[process.uuid] ?: run {
                    Napier.d { "Cron data not found with id: ${process.uuid}" }
                    return resp
                }

                jobs[process.uuid]?.let {
                    resp[process.uuid] = it.copy(
                        statuses = it.statuses.takeLast(50)
                    )
                }
            }
        }

        return resp
    }
}