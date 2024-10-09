package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.Task
import com.minidashboard.app.data.models.ProccessResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

private var parentJob: CompletableJob? = null
private const val RETRY_TOMORROW = 1 * 24 * 60 * 60 * 1000L

typealias Result = Pair<ProccessResult, Boolean>

data class CronItem(
    val task: Task,
    val statuses: List<Status>
)

enum class Status(val color: Color) {
    LAUNCHED(Color.Black),
    CORRECT(Color.Green),
    WARNING(Color.Yellow),
    ERROR(Color.Red),
}

var jobs: MutableMap<String, CronItem> = mutableMapOf()
    private set


fun isProcessesRunning(): Boolean {
    return parentJob?.isActive ?: false
}

// Function to run multiple processes with different times.
fun startProcesses(
    processes: List<Task>,
    onLaunched: (Task) -> Unit = {},
    onResult: (Result) -> Unit = {}
) {
    parentJob = Job()

    val job = parentJob ?: return
    val scope = CoroutineScope(Dispatchers.IO + job)
    Napier.d { "Starting the cron procceses with: ${processes.size}" }

    scope.launch {
        jobs = processes.map {
            it.uuid to CronItem(
                  task = it,
                  statuses = emptyList(),
                )
        }.toMap().toMutableMap()

        // For each process, launch a coroutine that runs at its specified interval
        for((key, proccess) in jobs){
            scope.async {
                // lose some time (at first launch) between launches to distribute requests.
                delay(Random.nextInt(250, 2000).toLong())

                while (true) {
                    val uuid = key
                    val item = jobs[uuid]
                    Napier.d { "Item: $item" }
                    val process = item?.copy(statuses = item.statuses + Status.LAUNCHED)
                    process?.let{
                        jobs[uuid] = process
                        Napier.d { "Item updated: ${jobs[uuid]}" }
                        onLaunched(process.task)

                        val task = item.task
                        task.execute { result ->
                            launch {
                                task.validate()
                                    .also { valid ->
                                        val copy = jobs[uuid]?.copy(
                                            statuses = process.statuses.dropLast(1) + when (valid) {
                                                true -> Status.CORRECT
                                                false -> Status.ERROR
                                            }
                                        )
                                        Napier.d { "copy: $copy" }

                                        copy?.let{
                                            jobs[uuid] = copy
                                            Napier.d { "copy updated on jobs: ${jobs[uuid]}" }
                                        }

                                        onResult(result to valid)
                                    }
                            }
                        }
                    }

                    Napier.i { "Next task with uuid: ${uuid} schedule in: ${item?.task?.schedule}" }.also {
                        delay(item?.task?.schedule?.nextLaunch() ?: RETRY_TOMORROW)
                    }
                }
            }
        }
    }
}

fun runSingleProcess(process: Task, result: (Result) -> Unit = {}) = GlobalScope.launch {
    process.execute { result ->
        launch {
            process.validate().also {valid ->
                result(result to valid)
            }
        }
    }
}

fun stopProcesses() {
    Napier.d { "Stop the cron procceses" }
    parentJob?.cancel("Cancel by user")
    parentJob = null
}