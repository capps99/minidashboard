package com.minidashboard.app.data.models

import com.minidashboard.db.cron.Cron
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ProjectModel(
    val uuid: String,
    val name: String,
    val description: String,
    val enabled: Boolean,
    val crons: List<CronModel> = emptyList(),
)

@Serializable
data class CronModel(
    val uuid: String?,
    val setup: String?,
    val status: Boolean,
    val history: List<CronHistoryModel> = emptyList()
){
    override fun equals(other: Any?): Boolean {
        if (other !is CronModel) return false
        return other.uuid == uuid
    }

}

@Serializable
data class CronHistoryModel(
    val timestamp: Long,
    val result: Boolean?,
)

/*
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
*/

fun CronModel.toJSON(): String = Json.encodeToString(this)

fun String.toCronModel(): CronModel = Json.decodeFromString<CronModel>(this)