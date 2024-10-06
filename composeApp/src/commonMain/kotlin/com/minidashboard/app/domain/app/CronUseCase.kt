package com.minidashboard.app.domain.app

import com.minidashboard.app.data.datasource.CronDataSource
import com.minidashboard.app.data.models.CronModel
import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.data.models.toCronProcess
import com.minidashboard.app.data.models.toJson
import io.github.aakira.napier.Napier

class CronUseCase(
    private val datasource: CronDataSource,
){

    fun insert(process: CronProcess) {
        val string = process.toJson()
        Napier.d { "string: $string" }
        datasource.insert(
            uuid = process.common.uuid,
            setup = process.toJson(),
            active = true
        )
    }


    fun list(): List<CronProcess> {
        return datasource.list().mapNotNull {
            it.toCronProcess()
        }
    }

    fun find(uuid: String): CronProcess? = list().find {
        it.uuid == uuid
    }

    fun delete(uuid: String){
        datasource.delete(uuid)
    }
}

fun CronModel.toCronProcess(): CronProcess? {
    return this.setup?.toCronProcess()
}