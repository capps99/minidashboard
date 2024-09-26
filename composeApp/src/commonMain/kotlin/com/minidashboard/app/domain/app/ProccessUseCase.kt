package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.ProccessResult
import io.github.aakira.napier.Napier
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.skiko.currentNanoTime

// Define a type alias for clarity: each process is a pair of a function and its interval time in seconds
typealias Process = Pair<CronProcess, Long>

// Function to run multiple processes with different times
fun runProcesses(processes: List<Process>, result: (ProccessResult) -> Unit = {}) = GlobalScope.launch {
    // For each process, launch a coroutine that runs at its specified interval
    for ((task, interval) in processes) {
        launch {
            while (true) {
                task.execute {
                    result(it)
                }
                delay(interval * 1000L)  // Wait for the specified interval (converted to milliseconds)
            }
        }
    }
}

fun runSingleProcess(process: CronProcess, result: (ProccessResult) -> Unit = {}) = GlobalScope.launch {
    process.execute {
        result(it)
    }
}

fun function(){
    Napier.d { "Procces N running at: ${currentNanoTime()}" }
}