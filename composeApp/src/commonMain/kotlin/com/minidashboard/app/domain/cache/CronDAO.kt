package com.minidashboard.app.domain.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.minidashboard.app.domain.persistence.DriverFactory
import com.minidashboard.app.domain.persistence.storage
import com.minidashboard.db.cron.Cron
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

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

    fun list(): Flow<List<Cron>> = database
            .cronQueries
            .select_cron()
            .asFlow()
            .mapToList(Dispatchers.IO)

}