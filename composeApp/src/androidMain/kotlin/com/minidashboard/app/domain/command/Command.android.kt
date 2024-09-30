package com.minidashboard.app.domain.command

actual class Command {
    actual fun execute(command: String, onResult: (CommandResult) -> Unit) {
        TODO("Not yet implemented")
    }
}