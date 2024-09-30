package com.minidashboard.app.domain.command

import io.github.aakira.napier.Napier
import java.io.BufferedReader
import java.io.InputStreamReader

actual class Command {
    actual fun execute(command: String, onResult: (CommandResult) -> Unit) {
        try {
            // Execute the shell command
            val process = Runtime.getRuntime().exec(command)

            // Create threads to handle both stdout and stderr
            val outputThread = Thread {
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    reader.lines().forEach { println(it) }
                }
            }

            val errorThread = Thread {
                BufferedReader(InputStreamReader(process.errorStream)).use { reader ->
                    reader.lines().forEach { System.err.println(it) }
                }
            }

            // Start threads
            outputThread.start()
            errorThread.start()

            // Wait for the process to complete
            val exitCode = process.waitFor()

            // Wait for both threads to finish
            outputThread.join()
            errorThread.join()

            Napier.d{ "Process finished with exit code: $exitCode" }

            onResult(CommandResult(exitCode, ""))
        } catch (e: Exception) {
            e.printStackTrace()
            onResult(CommandResult(-1, e.message.toString()))
        }
    }
}