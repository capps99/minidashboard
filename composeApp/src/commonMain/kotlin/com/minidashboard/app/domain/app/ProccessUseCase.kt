package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.ProccessResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import org.jetbrains.skiko.currentNanoTime

private var parentJob: CompletableJob? = null

// Function to run multiple processes with different times
fun startProcesses(processes: List<CronProcess>, result: (ProccessResult) -> Unit = {}) {
    parentJob = Job()

    val job = parentJob ?: return
    val scope = CoroutineScope(Dispatchers.IO + job)

    Napier.d { "Starting the cron procceses with: ${processes.size}" }

    scope.launch {
        // For each process, launch a coroutine that runs at its specified interval

        for (task in processes) {
            while (true) {
                task.execute {
                    result(it)
                }

                delay(task.common.nextLaunch() * 1000L)
            }
        }
    }
}

fun stopProcesses() {
    Napier.d { "Stop the cron procceses" }
    parentJob?.cancel("Cancel by user")
    parentJob = null
}

fun runSingleProcess(process: CronProcess, result: (ProccessResult) -> Unit = {}) = GlobalScope.launch {
    process.execute {
        result(it)
    }
}

fun function() {
    Napier.d { "Procces N running at: ${currentNanoTime()}" }
}