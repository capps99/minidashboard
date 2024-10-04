package com.minidashboard.app.domain.app

import io.github.aakira.napier.Antilog
import io.github.aakira.napier.LogLevel

expect class FileLogWriter() {
    fun writeLog(tag: String?, message: String)
}

// Custom Antilog implementation to write logs to a file
class FileLogger : Antilog() {
    private val fileLogWriter = FileLogWriter()

    override fun performLog(priority: LogLevel, tag: String?, throwable: Throwable?, message: String?) {
        val logMessage = "${priority.name}: [$tag] $message"
        fileLogWriter.writeLog(tag, logMessage)
    }
}