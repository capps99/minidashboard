package com.minidashboard.app.data.models

import com.minidashboard.db.cron.Cron
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class CronModel(
    val uuid: String?,
    val setup: String?,
    val status: Long?,
)

fun CronModel.toCron() = Cron(
    uuid = this.uuid,
    setup = this.setup,
    status = this.status
)

fun Cron.toCronModel() = CronModel(
    uuid = this.uuid,
    setup = this.setup,
    status = this.status
)

fun CronModel.toJSON(): String = Json.encodeToString(this)

fun String.toCronModel(): CronModel = Json.decodeFromString<CronModel>(this)