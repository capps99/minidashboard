package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.ProccessResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlin.random.Random

private var parentJob: CompletableJob? = null

typealias Result = Pair<ProccessResult, Boolean>

// Function to run multiple processes with different times.
fun startProcesses(processes: List<CronProcess>, result: (Result) -> Unit = {}) {
    parentJob = Job()

    val job = parentJob ?: return
    val scope = CoroutineScope(Dispatchers.IO + job)

    Napier.d { "Starting the cron procceses with: ${processes.size}" }

    scope.launch {
        // For each process, launch a coroutine that runs at its specified interval

        for (task in processes) {
            scope.async {
                // lose some time (at first launch) between launches to distribute requests.
                delay(Random.nextInt(250, 2000).toLong())

                while (true) {
                    task.execute { result ->
                        launch {
                            task.validate()
                                .also { valid ->
                                    result(result to valid)
                                }
                        }
                    }
                    Napier.i { "Next task with uuid: ${task.common.uuid} schedule in: ${task.schedule}" }.also {
                        delay(task.schedule.nextLaunch())
                    }
                }
            }
        }
    }
}

fun runSingleProcess(process: CronProcess, result: (Result) -> Unit = {}) = GlobalScope.launch {
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