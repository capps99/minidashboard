package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.toCronProcess
import com.minidashboard.app.data.models.toJson
import com.minidashboard.app.domain.cache.CronDAO
import com.minidashboard.db.cron.Cron
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlin.random.Random

class CronUseCase(
    private val dao: CronDAO
){

    fun insert(process: CronProcess) {
        val string = process.toJson()
        Napier.d { "string: $string" }
        dao.insert(
            uuid = process.common.uuid,
            setup = process.toJson(),
            active = true
        )
    }

    suspend fun list(): List<CronProcess> {
        return dao.list().first().mapNotNull {
            it.toCronProcess()
        }
    }
}

fun Cron.toCronProcess(): CronProcess? {
    return this.setup?.toCronProcess()
}