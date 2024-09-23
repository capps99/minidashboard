package com.minidashboard.app.domain.cache

import com.minidashboard.app.domain.persistence.DriverFactory
import com.minidashboard.app.domain.persistence.storage
import com.minidashboard.db.cron.Cron
import io.github.aakira.napier.Napier

class CronDAO (driverFactory: DriverFactory){

    private val database = storage(driverFactory)

    fun insert(
        uuid: String,
        setup: String,
        active: Boolean
    ){
        Napier.d {"Creating cron."}
        database.cronQueries.insert_cron(
            uuid = uuid,
            setup = setup,
            status = if(active) 1L else 0L
        )
    }

    fun list(): List<Cron> {
        Napier.d {"Searching all crons"}
        val resp = database.cronQueries.select_cron().executeAsList()
        Napier.d {"Cron size: ${resp.size}"}
        return resp
    }
}