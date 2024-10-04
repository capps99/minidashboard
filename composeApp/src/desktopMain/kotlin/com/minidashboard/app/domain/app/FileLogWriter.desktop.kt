package com.minidashboard.app.domain.app

import java.io.File
import java.io.FileWriter
import java.io.IOException

actual class FileLogWriter actual constructor() {
    private val logFile: File = File("/Users/cap/Downloads/logs/app_logs.txt")

    init {
        // Ensure the log directory and file exist
        if (!logFile.parentFile.exists()) {
            logFile.parentFile.mkdirs()
        }

        if (!logFile.exists()) {
            logFile.createNewFile()
        }
    }

    actual fun writeLog(tag: String?, message: String) {
        try {
            FileWriter(logFile, true).use { writer ->
                writer.appendLine("$tag: $message")
            }
        } catch (e: IOException) {
            println("Error writing log: ${e.message}")
        }
    }
}