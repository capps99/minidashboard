package com.minidashboard.app.domain.command

/**
 * First is udsed fior code, second for the message.
 */
typealias CommandResult = Pair<Int, String>

expect class Command() {
    fun execute(command: String, onResult: (CommandResult) -> Unit)
}