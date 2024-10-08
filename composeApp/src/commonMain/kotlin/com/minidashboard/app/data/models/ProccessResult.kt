package com.minidashboard.app.data.models

sealed interface ProccessResult {
    val uuid: String
}

data class HttpResult(
    val proccess: Task,
    val code: Int,
    val message: String
): ProccessResult {
    override val uuid = proccess.common.uuid
}


data class CommandResult(
    val proccess: Task,
    val code: Int,
    val message: String
): ProccessResult {
    override val uuid = proccess.common.uuid
}