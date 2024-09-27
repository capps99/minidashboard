package com.minidashboard.app.data.models

sealed interface ProccessResult

data class HttpResult(
    val code: Int,
    val message: String,
): ProccessResult