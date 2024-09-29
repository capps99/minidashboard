package com.minidashboard.app.data.models

sealed interface ProccessResult {
    val uuid: String
}

data class HttpResult(
    val proccess: CronProcess,
    val code: Int,
    val message: String
): ProccessResult {
    override val uuid = proccess.common.uuid
}