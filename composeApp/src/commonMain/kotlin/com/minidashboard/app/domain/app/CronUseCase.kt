package com.minidashboard.app.domain.app

import com.minidashboard.app.data.models.CronProcess
import com.minidashboard.app.domain.cache.CronDAO
import com.minidashboard.db.cron.Cron
import kotlin.random.Random

class CronUseCase(
    private val dao: CronDAO
){

    fun insert(process: CronProcess) {
        dao.insert(
            uuid = Random.nextInt(0, 100).toString(),
            setup = "title=${process.cronCommmon.title}",
            active = true
        )
    }

    fun list(): List<Cron> {
        return dao.list()
    }


}